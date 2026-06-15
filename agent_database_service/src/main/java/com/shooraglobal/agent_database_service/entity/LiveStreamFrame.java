package com.shooraglobal.agent_database_service.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

import java.time.LocalDateTime;

@Entity
@Table(
        name = "live_stream_frames",
        indexes = {
                @Index(name = "idx_live_stream_device_created", columnList = "device_id, created_at"),
                @Index(name = "idx_live_stream_session_frame", columnList = "session_id, frame_index")
        }
)
public class LiveStreamFrame {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "device_id", nullable = false)
    private Device device;

    @Column(name = "session_id", length = 150)
    private String sessionId;

    @Column(name = "frame_index")
    private Long frameIndex;

    @Column(name = "stream_fps")
    private Integer streamFps;

    @Column(name = "capture_time")
    private LocalDateTime captureTime;

    @Column(name = "captured_at_utc", length = 80)
    private String capturedAtUtc;

    @Column(name = "capture_type", length = 80)
    private String captureType;

    @Column(name = "image_path", length = 700, nullable = false)
    private String imagePath;

    @Column(name = "content_type", length = 100, nullable = false)
    private String contentType;

    @Column(name = "file_size_bytes")
    private Long fileSizeBytes;

    @Column(name = "ip_address", length = 80)
    private String ipAddress;

    @Column(name = "city", length = 100)
    private String city;

    @Column(name = "created_at")
    private LocalDateTime createdAt = LocalDateTime.now();

    public Long getId() {
        return id;
    }

    public Device getDevice() {
        return device;
    }

    public void setDevice(Device device) {
        this.device = device;
    }

    public String getSessionId() {
        return sessionId;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }

    public Long getFrameIndex() {
        return frameIndex;
    }

    public void setFrameIndex(Long frameIndex) {
        this.frameIndex = frameIndex;
    }

    public Integer getStreamFps() {
        return streamFps;
    }

    public void setStreamFps(Integer streamFps) {
        this.streamFps = streamFps;
    }

    public LocalDateTime getCaptureTime() {
        return captureTime;
    }

    public void setCaptureTime(LocalDateTime captureTime) {
        this.captureTime = captureTime;
    }

    public String getCapturedAtUtc() {
        return capturedAtUtc;
    }

    public void setCapturedAtUtc(String capturedAtUtc) {
        this.capturedAtUtc = capturedAtUtc;
    }

    public String getCaptureType() {
        return captureType;
    }

    public void setCaptureType(String captureType) {
        this.captureType = captureType;
    }

    public String getImagePath() {
        return imagePath;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }

    public String getContentType() {
        return contentType;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    public Long getFileSizeBytes() {
        return fileSizeBytes;
    }

    public void setFileSizeBytes(Long fileSizeBytes) {
        this.fileSizeBytes = fileSizeBytes;
    }

    public String getIpAddress() {
        return ipAddress;
    }

    public void setIpAddress(String ipAddress) {
        this.ipAddress = ipAddress;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
}
