package com.shooraglobal.agent_database_service.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.LocalDateTime;

public class LiveStreamUploadResponseDto {

    private String message;

    @JsonProperty("device_id")
    private Long deviceId;

    @JsonProperty("frame_id")
    private Long frameId;

    @JsonProperty("session_id")
    private String sessionId;

    @JsonProperty("frame_index")
    private Long frameIndex;

    @JsonProperty("capture_time")
    private LocalDateTime captureTime;

    public LiveStreamUploadResponseDto() {
    }

    public LiveStreamUploadResponseDto(
            String message,
            Long deviceId,
            Long frameId,
            String sessionId,
            Long frameIndex,
            LocalDateTime captureTime
    ) {
        this.message = message;
        this.deviceId = deviceId;
        this.frameId = frameId;
        this.sessionId = sessionId;
        this.frameIndex = frameIndex;
        this.captureTime = captureTime;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Long getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(Long deviceId) {
        this.deviceId = deviceId;
    }

    public Long getFrameId() {
        return frameId;
    }

    public void setFrameId(Long frameId) {
        this.frameId = frameId;
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

    public LocalDateTime getCaptureTime() {
        return captureTime;
    }

    public void setCaptureTime(LocalDateTime captureTime) {
        this.captureTime = captureTime;
    }
}
