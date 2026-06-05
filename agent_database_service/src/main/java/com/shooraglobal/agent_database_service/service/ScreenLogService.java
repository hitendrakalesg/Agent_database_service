package com.shooraglobal.agent_database_service.service;

import com.shooraglobal.agent_database_service.dto.DeviceResponseDto;
import com.shooraglobal.agent_database_service.dto.ScreenLogRequestDto;

import com.shooraglobal.agent_database_service.dto.ScreenLogResponseDto;
import com.shooraglobal.agent_database_service.entity.Device;
import com.shooraglobal.agent_database_service.entity.ScreenLog;
import com.shooraglobal.agent_database_service.exception.AgentDatabaseServiceException;
import com.shooraglobal.agent_database_service.repo.DeviceRepo;
import com.shooraglobal.agent_database_service.repo.ScreenLogRepo;
import com.shooraglobal.agent_database_service.util.FileUtil;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.util.UriUtils;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;




@Service
public class ScreenLogService {
    private  final DeviceRepo deviceRepo;
    private final ScreenLogRepo screenLogRepo;
    private final FileUtil fileUtil;


    public ScreenLogService(DeviceRepo deviceRepo, ScreenLogRepo screenLogRepo, FileUtil fileUtil) {
        this.deviceRepo = deviceRepo;
        this.screenLogRepo = screenLogRepo;

        this.fileUtil = fileUtil;
    }
    @Transactional
    public String saveScreenLog(ScreenLogRequestDto dto, MultipartFile file) {

        if (file.isEmpty()) {
            throw new AgentDatabaseServiceException("Screenshot file is required");
        }

        String type = file.getContentType();

        if (type == null || !type.startsWith("image/")) {
            throw new AgentDatabaseServiceException("Wrong File Type!");
        }

        String companyName = normalizeRequired(dto.getCompanyName(), "companyName");
        String macAddress = normalizeRequired(dto.getMacAddress(), "macAddress");
        String employeeName = normalizeRequired(dto.getEmployeeName(), "employeeName");

        dto.setCompanyName(companyName);
        dto.setMacAddress(macAddress);
        dto.setEmployeeName(employeeName);

        Device device = deviceRepo
                .findByCompanyNameIgnoreCaseAndMacAddressIgnoreCase(companyName, macAddress)
                .orElseGet(Device::new);

        device.setUsername(trimOptional(dto.getUsername()));

        device.setComputerName(trimOptional(dto.getComputerName()));

        device.setMacAddress(macAddress);

        device.setCompanyName(companyName);

        device.setClientName(trimOptional(dto.getClientName()));

        device.setEmployeeName(employeeName);

        device.setRegisteredUserName(trimOptional(dto.getRegisteredUserName()));

        device.setCity(trimOptional(dto.getCity()));

        device.setProductKey(trimOptional(dto.getProductKey()));

        device.setRegisteredUrl(trimOptional(dto.getRegisteredUrl()));

        device.setLastScreenShotCaptureAt(dto.getCaptureTime());

        device = deviceRepo.save(device);



//      creating folder to store screenshot

        Path imagePath= null;
        try {
            imagePath = fileUtil.createFile(dto,file);
        } catch (IOException e) {
            throw new AgentDatabaseServiceException("Error in Creating File");
        }

        // SAVE DB RECORD

        ScreenLog screenLog = new ScreenLog();

        screenLog.setDevice(device);
        screenLog.setCaptureTime(dto.getCaptureTime());
        screenLog.setImagePath(imagePath.toString());

        screenLogRepo.save(screenLog);


        return "Screen Log Uploaded Successfully.";





    }


    public List<DeviceResponseDto> getAllDevices(String companyName) {

        List<Device> devices = deviceRepo.findByCompanyNameIgnoreCaseOrderByEmployeeNameAscComputerNameAsc(
                normalizeRequired(companyName, "companyName")
        );

        return devices.stream()
                .map(device -> new DeviceResponseDto(


                        device.getId(),
                        device.getUsername(),
                        device.getComputerName(),
                        device.getMacAddress(),
                        device.getCompanyName(),
                        device.getClientName(),
                        device.getEmployeeName(),
                        device.getRegisteredUserName(),
                        device.getCity(),
                        device.getProductKey(),
                        device.getRegisteredUrl(),
                        device.getLastScreenShotCaptureAt()

                ))
                .toList();

    }

    public List<ScreenLogResponseDto> getScreenLogs(String companyName, Long deviceId,String date) {

        String normalizedCompanyName = normalizeRequired(companyName, "companyName");

        LocalDate localDate = LocalDate.parse(date);

        LocalDateTime start = localDate.atStartOfDay();

        LocalDateTime end = localDate.plusDays(1).atStartOfDay();

        List<ScreenLog> logs =
                screenLogRepo.findByDevice_IdAndDevice_CompanyNameIgnoreCaseAndCaptureTimeBetween(
                        deviceId,
                        normalizedCompanyName,
                        start,
                        end
                );

        String encodedCompanyName = UriUtils.encodePathSegment(normalizedCompanyName, StandardCharsets.UTF_8);

        return logs.stream()
                .map(log -> new ScreenLogResponseDto(

                        log.getId(),

                        "/api/screenlogs/companies/" + encodedCompanyName + "/devices/" + deviceId + "/images/" + log.getId(),

                        log.getCaptureTime()

                ))
                .toList();
    }

    public Resource getImage(
            String companyName,
            Long imageId,
            Long deviceId
    ) throws IOException {

        String normalizedCompanyName = normalizeRequired(companyName, "companyName");

        ScreenLog screenLog =
                screenLogRepo
                        .findByIdAndDevice_IdAndDevice_CompanyNameIgnoreCase(
                                imageId,
                                deviceId,
                                normalizedCompanyName
                        )
                        .orElseThrow(() ->
                                new AgentDatabaseServiceException(
                                        "Image not found"
                                )
                        );

        Path path = Paths.get(
                screenLog.getImagePath()
        );

        if (!Files.exists(path)) {

            throw new AgentDatabaseServiceException(
                    "Image file not found"
            );
        }

        return new UrlResource(path.toUri());
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
}
