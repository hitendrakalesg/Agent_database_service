package com.shooraglobal.agent_database_service.dto;

import java.time.LocalDateTime;

public class DeviceResponseDto {
    private long id;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    private String username;

    private String computerName;

    private String macAddress;

    private LocalDateTime lastScreenShotCaptureAt;

    public DeviceResponseDto(String username, String computerName, String macAddress, LocalDateTime lastScreenShotCaptureAt) {
        this.username = username;
        this.computerName = computerName;
        this.macAddress = macAddress;
        this.lastScreenShotCaptureAt = lastScreenShotCaptureAt;
    }

    public DeviceResponseDto(long id, String username, String computerName, String macAddress, LocalDateTime lastScreenShotCaptureAt) {
        this.id = id;
        this.username = username;
        this.computerName = computerName;
        this.macAddress = macAddress;
        this.lastScreenShotCaptureAt = lastScreenShotCaptureAt;
    }

    public DeviceResponseDto() {
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getComputerName() {
        return computerName;
    }

    public void setComputerName(String computerName) {
        this.computerName = computerName;
    }

    public String getMacAddress() {
        return macAddress;
    }

    public void setMacAddress(String macAddress) {
        this.macAddress = macAddress;
    }

    public LocalDateTime getLastScreenShotCaptureAt() {
        return lastScreenShotCaptureAt;
    }

    public void setLastScreenShotCaptureAt(LocalDateTime lastScreenShotCaptureAt) {
        this.lastScreenShotCaptureAt = lastScreenShotCaptureAt;
    }
}
