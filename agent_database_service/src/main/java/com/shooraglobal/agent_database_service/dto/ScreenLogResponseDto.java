package com.shooraglobal.agent_database_service.dto;

import java.time.LocalDateTime;

public class ScreenLogResponseDto {

    private Long id;

    private String imageUrl;

    private LocalDateTime captureTime;

    public ScreenLogResponseDto() {
    }

    public ScreenLogResponseDto(
            Long id,
            String imageUrl,
            LocalDateTime captureTime
    ) {
        this.id = id;
        this.imageUrl = imageUrl;
        this.captureTime = captureTime;
    }

    public Long getId() {
        return id;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public LocalDateTime getCaptureTime() {
        return captureTime;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public void setCaptureTime(LocalDateTime captureTime) {
        this.captureTime = captureTime;
    }
}