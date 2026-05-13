package com.shooraglobal.agent_database_service.dto;

import java.util.Map;

public class ScreenLogResponseDto {
    private String username;

    private String computerName;

    private String macAddress;

    private Map<String,byte[]> screenshots;


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

    public Map<String, byte[]> getScreenshots() {
        return screenshots;
    }

    public void setScreenshots(Map<String, byte[]> screenshots) {
        this.screenshots = screenshots;
    }

    public ScreenLogResponseDto(String username, String computerName, String macAddress, Map<String, byte[]> screenshots) {
        this.username = username;
        this.computerName = computerName;
        this.macAddress = macAddress;
        this.screenshots = screenshots;
    }

    public ScreenLogResponseDto(){}
}
