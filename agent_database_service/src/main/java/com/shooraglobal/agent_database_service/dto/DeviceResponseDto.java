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

    private String companyName;

    private String clientName;

    private String employeeName;

    private String registeredUserName;

    private String city;

    private String productKey;

    private String registeredUrl;

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

    public DeviceResponseDto(
            long id,
            String username,
            String computerName,
            String macAddress,
            String companyName,
            String clientName,
            String employeeName,
            String registeredUserName,
            String city,
            String productKey,
            String registeredUrl,
            LocalDateTime lastScreenShotCaptureAt
    ) {
        this.id = id;
        this.username = username;
        this.computerName = computerName;
        this.macAddress = macAddress;
        this.companyName = companyName;
        this.clientName = clientName;
        this.employeeName = employeeName;
        this.registeredUserName = registeredUserName;
        this.city = city;
        this.productKey = productKey;
        this.registeredUrl = registeredUrl;
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

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    public String getClientName() {
        return clientName;
    }

    public void setClientName(String clientName) {
        this.clientName = clientName;
    }

    public String getEmployeeName() {
        return employeeName;
    }

    public void setEmployeeName(String employeeName) {
        this.employeeName = employeeName;
    }

    public String getRegisteredUserName() {
        return registeredUserName;
    }

    public void setRegisteredUserName(String registeredUserName) {
        this.registeredUserName = registeredUserName;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getProductKey() {
        return productKey;
    }

    public void setProductKey(String productKey) {
        this.productKey = productKey;
    }

    public String getRegisteredUrl() {
        return registeredUrl;
    }

    public void setRegisteredUrl(String registeredUrl) {
        this.registeredUrl = registeredUrl;
    }

    public LocalDateTime getLastScreenShotCaptureAt() {
        return lastScreenShotCaptureAt;
    }

    public void setLastScreenShotCaptureAt(LocalDateTime lastScreenShotCaptureAt) {
        this.lastScreenShotCaptureAt = lastScreenShotCaptureAt;
    }
}
