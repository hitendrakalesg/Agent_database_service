package com.shooraglobal.agent_database_service.service;

import com.shooraglobal.agent_database_service.dto.LiveStreamFrameRequestDto;
import com.shooraglobal.agent_database_service.dto.LiveStreamUploadResponseDto;
import com.shooraglobal.agent_database_service.entity.Device;
import com.shooraglobal.agent_database_service.entity.LiveStreamFrame;
import com.shooraglobal.agent_database_service.exception.AgentDatabaseServiceException;
import com.shooraglobal.agent_database_service.repo.DeviceRepo;
import com.shooraglobal.agent_database_service.repo.LiveStreamFrameRepo;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.MultiValueMap;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

@Service
public class LiveStreamService {

    public static final String MJPEG_BOUNDARY = "sgfortress-live-frame";

    private static final Duration FRAME_WAIT_TIMEOUT = Duration.ofSeconds(20);

    private final DeviceRepo deviceRepo;
    private final LiveStreamFrameRepo liveStreamFrameRepo;
    private final ConcurrentMap<Long, FrameBuffer> frameBuffers = new ConcurrentHashMap<>();

    @Value("${storage.path}")
    private String storagePath;

    public LiveStreamService(DeviceRepo deviceRepo, LiveStreamFrameRepo liveStreamFrameRepo) {
        this.deviceRepo = deviceRepo;
        this.liveStreamFrameRepo = liveStreamFrameRepo;
    }

    @Transactional
    public LiveStreamUploadResponseDto saveFrame(MultiValueMap<String, String> fields, MultipartFile file) {
        return saveFrame(toRequestDto(fields), file);
    }

    @Transactional
    public LiveStreamUploadResponseDto saveFrame(LiveStreamFrameRequestDto dto, MultipartFile file) {
        if (dto == null) {
            throw new AgentDatabaseServiceException("Live stream metadata is required");
        }

        validateImage(file);

        String companyName = normalizeRequired(dto.getCompanyName(), "company_name");
        String workspaceCode = normalizeRequired(dto.getWorkspaceCode(), "workspace_code");
        String userName = normalizeRequired(dto.getUserName(), "user_name");
        String email = normalizeRequired(dto.getEmail(), "email");
        String hostname = normalizeRequired(dto.getHostname(), "hostname");
        String macAddress = normalizeRequired(dto.getMacAddress(), "mac_address");
        String deviceToken = normalizeRequired(dto.getDeviceToken(), "device_token");
        LocalDateTime captureTime = dto.getCaptureTime() == null ? LocalDateTime.now() : dto.getCaptureTime();
        String sessionId = firstNonBlank(dto.getSessionId(), "session-" + captureTime.format(DateTimeFormatter.BASIC_ISO_DATE));
        long frameIndex = dto.getFrameIndex() == null ? 0L : Math.max(0L, dto.getFrameIndex());
        int streamFps = dto.getStreamFps() == null ? 1 : Math.max(1, dto.getStreamFps());

        Device device = deviceRepo
                .findByCompanyNameIgnoreCaseAndDeviceTokenIgnoreCase(companyName, deviceToken)
                .orElseGet(Device::new);

        device.setCompanyName(companyName);
        device.setWorkspaceCode(workspaceCode);
        device.setUserName(userName);
        device.setEmail(email);
        device.setHostname(hostname);
        device.setMacAddress(macAddress);
        device.setProductKey(trimOptional(dto.getProductKey()));
        device.setRegisteredUrl(trimOptional(dto.getRegisteredUrl()));
        device.setDeviceToken(deviceToken);
        device = deviceRepo.save(device);

        byte[] frameBytes = readFileBytes(file);
        String contentType = file.getContentType() == null ? "image/jpeg" : file.getContentType().trim();
        Path framePath = writeFrameFile(dto, companyName, userName, captureTime, sessionId, frameIndex, contentType, frameBytes);

        LiveStreamFrame frame = new LiveStreamFrame();
        frame.setDevice(device);
        frame.setSessionId(sessionId);
        frame.setFrameIndex(frameIndex);
        frame.setStreamFps(streamFps);
        frame.setCaptureTime(captureTime);
        frame.setCapturedAtUtc(trimOptional(dto.getCapturedAtUtc()));
        frame.setCaptureType(firstNonBlank(dto.getCaptureType(), "live_stream_frame"));
        frame.setImagePath(framePath.toString());
        frame.setContentType(contentType);
        frame.setFileSizeBytes((long) frameBytes.length);
        frame.setIpAddress(trimOptional(dto.getIpAddress()));
        frame.setCity(trimOptional(dto.getCity()));
        frame = liveStreamFrameRepo.save(frame);

        publishLatestFrame(device.getId(), toFrameData(frame, companyName, frameBytes));

        return new LiveStreamUploadResponseDto(
                "Live stream frame uploaded successfully.",
                device.getId(),
                frame.getId(),
                frame.getSessionId(),
                frame.getFrameIndex(),
                frame.getCaptureTime()
        );
    }

    @Transactional(readOnly = true)
    public LatestFrameResource getLatestFrameResource(String companyName, Long deviceId) {
        String normalizedCompanyName = normalizeRequired(companyName, "company_name");
        validateDevice(normalizedCompanyName, deviceId);

        LiveStreamFrame frame = liveStreamFrameRepo
                .findTopByDevice_IdAndDevice_CompanyNameIgnoreCaseOrderByCreatedAtDesc(deviceId, normalizedCompanyName)
                .orElseThrow(() -> new AgentDatabaseServiceException("Live stream frame not found"));

        Path framePath = Paths.get(frame.getImagePath());
        if (!Files.exists(framePath)) {
            throw new AgentDatabaseServiceException("Live stream image file not found");
        }

        try {
            return new LatestFrameResource(
                    new UrlResource(framePath.toUri()),
                    frame.getContentType(),
                    framePath.getFileName().toString(),
                    Files.size(framePath)
            );
        } catch (IOException e) {
            throw new AgentDatabaseServiceException("Error reading live stream image file", e);
        }
    }

    public StreamingResponseBody streamVideo(String companyName, Long deviceId) {
        String normalizedCompanyName = normalizeRequired(companyName, "company_name");
        validateDevice(normalizedCompanyName, deviceId);
        seedLatestFrame(normalizedCompanyName, deviceId);

        return outputStream -> {
            Long lastFrameId = null;
            FrameBuffer frameBuffer = frameBufferFor(deviceId);

            while (!Thread.currentThread().isInterrupted()) {
                FrameData frameData = frameBuffer.waitForNextFrame(lastFrameId, FRAME_WAIT_TIMEOUT);

                if (frameData == null) {
                    seedLatestFrame(normalizedCompanyName, deviceId);
                    frameData = frameBuffer.waitForNextFrame(lastFrameId, Duration.ofMillis(1));
                }

                if (frameData == null) {
                    continue;
                }

                writeMjpegFrame(outputStream, frameData);
                lastFrameId = frameData.id();
            }
        };
    }

    private void validateImage(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new AgentDatabaseServiceException("Live stream frame file is required");
        }

        String contentType = file.getContentType();
        if (contentType == null || !contentType.toLowerCase(Locale.ROOT).startsWith("image/")) {
            throw new AgentDatabaseServiceException("Wrong live stream frame file type");
        }
    }

    private byte[] readFileBytes(MultipartFile file) {
        try {
            return file.getBytes();
        } catch (IOException e) {
            throw new AgentDatabaseServiceException("Error reading live stream frame file", e);
        }
    }

    private Path writeFrameFile(
            LiveStreamFrameRequestDto dto,
            String companyName,
            String userName,
            LocalDateTime captureTime,
            String sessionId,
            long frameIndex,
            String contentType,
            byte[] frameBytes
    ) {
        Path folderPath = Paths.get(storagePath)
                .resolve("live-stream")
                .resolve(sanitize(companyName))
                .resolve(sanitize(userName))
                .resolve(captureTime.toLocalDate().toString())
                .resolve(sanitize(sessionId));

        String fileName = frameIndex > 0
                ? String.format("frame-%08d%s", frameIndex, getFileExtension(dto, contentType))
                : "frame-" + System.currentTimeMillis() + getFileExtension(dto, contentType);

        try {
            Files.createDirectories(folderPath);
            Path framePath = folderPath.resolve(fileName);
            Files.write(
                    framePath,
                    frameBytes,
                    StandardOpenOption.CREATE,
                    StandardOpenOption.TRUNCATE_EXISTING,
                    StandardOpenOption.WRITE
            );
            return framePath;
        } catch (IOException e) {
            throw new AgentDatabaseServiceException("Error saving live stream frame file", e);
        }
    }

    private LiveStreamFrameRequestDto toRequestDto(MultiValueMap<String, String> fields) {
        LiveStreamFrameRequestDto dto = new LiveStreamFrameRequestDto();

        dto.setCompanyName(firstField(fields, "company_name", "companyName"));
        dto.setWorkspaceCode(firstField(fields, "workspace_code", "workspaceCode"));
        dto.setUserName(firstField(fields, "user_name", "userName", "username", "registered_user_name"));
        dto.setEmail(firstField(fields, "email"));
        dto.setHostname(firstField(fields, "hostname", "computerName", "device_name"));
        dto.setMacAddress(firstField(fields, "mac_address", "macAddress"));
        dto.setCaptureTime(parseLocalDateTime(firstField(fields, "capture_time", "captureTime"), "capture_time"));
        dto.setProductKey(firstField(fields, "product_key", "productKey"));
        dto.setRegisteredUrl(firstField(fields, "registered_url", "registeredUrl"));
        dto.setDeviceToken(firstField(fields, "device_token", "deviceToken"));
        dto.setCaptureType(firstField(fields, "capture_type", "captureType"));
        dto.setCapturedAtUtc(firstField(fields, "captured_at_utc", "capturedAtUtc"));
        dto.setSessionId(firstField(fields, "session_id", "sessionId"));
        dto.setFrameIndex(parseLong(firstField(fields, "frame_index", "frameIndex"), "frame_index"));
        dto.setStreamFps(parseInteger(firstField(fields, "stream_fps", "streamFps"), "stream_fps"));
        dto.setIpAddress(firstField(fields, "ip_address", "ipAddress"));
        dto.setCity(firstField(fields, "city"));
        dto.setSiteToken(firstField(fields, "site_token", "siteToken"));

        return dto;
    }

    private String firstField(MultiValueMap<String, String> fields, String... names) {
        if (fields == null) {
            return null;
        }

        for (String name : names) {
            String value = fields.getFirst(name);
            if (value != null && !value.isBlank()) {
                return value.trim();
            }
        }

        return null;
    }

    private LocalDateTime parseLocalDateTime(String value, String fieldName) {
        if (value == null || value.isBlank()) {
            return null;
        }

        try {
            return LocalDateTime.parse(value.trim());
        } catch (RuntimeException e) {
            throw new AgentDatabaseServiceException(fieldName + " must be an ISO local date-time", e);
        }
    }

    private Long parseLong(String value, String fieldName) {
        if (value == null || value.isBlank()) {
            return null;
        }

        try {
            return Long.parseLong(value.trim());
        } catch (NumberFormatException e) {
            throw new AgentDatabaseServiceException(fieldName + " must be a number", e);
        }
    }

    private Integer parseInteger(String value, String fieldName) {
        if (value == null || value.isBlank()) {
            return null;
        }

        try {
            return Integer.parseInt(value.trim());
        } catch (NumberFormatException e) {
            throw new AgentDatabaseServiceException(fieldName + " must be a number", e);
        }
    }

    private void seedLatestFrame(String companyName, Long deviceId) {
        Optional<LiveStreamFrame> latestFrame = liveStreamFrameRepo
                .findTopByDevice_IdAndDevice_CompanyNameIgnoreCaseOrderByCreatedAtDesc(deviceId, companyName);

        latestFrame.ifPresent(frame -> {
            FrameBuffer frameBuffer = frameBufferFor(deviceId);
            FrameData currentFrame = frameBuffer.current();
            if (currentFrame != null && currentFrame.id().equals(frame.getId())) {
                return;
            }

            Path framePath = Paths.get(frame.getImagePath());
            if (!Files.exists(framePath)) {
                return;
            }

            try {
                publishLatestFrame(deviceId, toFrameData(frame, companyName, Files.readAllBytes(framePath)));
            } catch (IOException ignored) {
                // The next successful upload will refresh the in-memory frame.
            }
        });
    }

    private FrameData toFrameData(LiveStreamFrame frame, String companyName, byte[] frameBytes) {
        return new FrameData(
                frame.getId(),
                frame.getDevice().getId(),
                companyName,
                frame.getContentType(),
                frame.getSessionId(),
                frame.getFrameIndex(),
                frame.getCaptureTime(),
                frameBytes
        );
    }

    private void publishLatestFrame(Long deviceId, FrameData frameData) {
        frameBufferFor(deviceId).publish(frameData);
    }

    private FrameBuffer frameBufferFor(Long deviceId) {
        return frameBuffers.computeIfAbsent(deviceId, ignored -> new FrameBuffer());
    }

    private void writeMjpegFrame(OutputStream outputStream, FrameData frameData) throws IOException {
        String header = "--" + MJPEG_BOUNDARY + "\r\n"
                + "Content-Type: " + frameData.contentType() + "\r\n"
                + "Content-Length: " + frameData.bytes().length + "\r\n"
                + "X-Frame-Id: " + frameData.id() + "\r\n"
                + "X-Session-Id: " + blankToEmpty(frameData.sessionId()) + "\r\n"
                + "X-Frame-Index: " + frameData.frameIndex() + "\r\n\r\n";

        outputStream.write(header.getBytes(StandardCharsets.UTF_8));
        outputStream.write(frameData.bytes());
        outputStream.write("\r\n".getBytes(StandardCharsets.UTF_8));
        outputStream.flush();
    }

    private void validateDevice(String companyName, Long deviceId) {
        if (deviceId == null) {
            throw new AgentDatabaseServiceException("device_id is required");
        }

        Device device = deviceRepo
                .findById(deviceId)
                .orElseThrow(() -> new AgentDatabaseServiceException("Device not found"));

        if (device.getCompanyName() == null || !device.getCompanyName().equalsIgnoreCase(companyName)) {
            throw new AgentDatabaseServiceException("Device not found for company");
        }
    }

    private String getFileExtension(LiveStreamFrameRequestDto dto, String contentType) {
        if ("image/jpeg".equalsIgnoreCase(contentType) || "image/jpg".equalsIgnoreCase(contentType)) {
            return ".jpg";
        }

        if ("image/png".equalsIgnoreCase(contentType)) {
            return ".png";
        }

        return ".img";
    }

    private String normalizeRequired(String value, String fieldName) {
        if (value == null || value.isBlank()) {
            throw new AgentDatabaseServiceException(fieldName + " is required");
        }

        return value.trim();
    }

    private String trimOptional(String value) {
        if (value == null) {
            return null;
        }

        String trimmedValue = value.trim();
        return trimmedValue.isEmpty() ? null : trimmedValue;
    }

    private String firstNonBlank(String... values) {
        for (String value : values) {
            if (value != null && !value.isBlank()) {
                return value.trim();
            }
        }

        return "";
    }

    private String sanitize(String value) {
        if (value == null || value.isBlank()) {
            return "unknown";
        }

        return value.replaceAll("[^a-zA-Z0-9-_]", "_");
    }

    private String blankToEmpty(String value) {
        return value == null ? "" : value;
    }

    public record LatestFrameResource(Resource resource, String contentType, String fileName, long contentLength) {
    }

    private record FrameData(
            Long id,
            Long deviceId,
            String companyName,
            String contentType,
            String sessionId,
            Long frameIndex,
            LocalDateTime captureTime,
            byte[] bytes
    ) {
    }

    private static final class FrameBuffer {

        private FrameData latestFrame;

        synchronized void publish(FrameData frameData) {
            latestFrame = frameData;
            notifyAll();
        }

        synchronized FrameData current() {
            return latestFrame;
        }

        synchronized FrameData waitForNextFrame(Long lastFrameId, Duration timeout) {
            long deadline = System.currentTimeMillis() + timeout.toMillis();

            while (latestFrame == null || latestFrame.id().equals(lastFrameId)) {
                long waitMillis = deadline - System.currentTimeMillis();
                if (waitMillis <= 0) {
                    return null;
                }

                try {
                    wait(waitMillis);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    return null;
                }
            }

            return latestFrame;
        }
    }
}
