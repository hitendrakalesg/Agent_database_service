package com.shooraglobal.agent_database_service.dto;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.LocalDateTime;

public class LiveStreamFrameRequestDto {

    @JsonProperty("company_name")
    @JsonAlias("companyName")
    private String companyName;

    @JsonProperty("workspace_code")
    @JsonAlias("workspaceCode")
    private String workspaceCode;

    @JsonProperty("user_name")
    @JsonAlias({"userName", "username", "registered_user_name"})
    private String userName;

    @JsonProperty("email")
    private String email;

    @JsonProperty("hostname")
    @JsonAlias({"computerName", "device_name"})
    private String hostname;

    @JsonProperty("mac_address")
    @JsonAlias("macAddress")
    private String macAddress;

    @JsonProperty("capture_time")
    @JsonAlias("captureTime")
    private LocalDateTime captureTime;

    @JsonProperty("product_key")
    @JsonAlias("productKey")
    private String productKey;

    @JsonProperty("registered_url")
    @JsonAlias("registeredUrl")
    private String registeredUrl;

    @JsonProperty("device_token")
    @JsonAlias("deviceToken")
    private String deviceToken;

    @JsonProperty("capture_type")
    @JsonAlias("captureType")
    private String captureType;

    @JsonProperty("captured_at_utc")
    @JsonAlias("capturedAtUtc")
    private String capturedAtUtc;

    @JsonProperty("session_id")
    @JsonAlias("sessionId")
    private String sessionId;

    @JsonProperty("frame_index")
    @JsonAlias("frameIndex")
    private Long frameIndex;

    @JsonProperty("stream_fps")
    @JsonAlias("streamFps")
    private Integer streamFps;

    @JsonProperty("ip_address")
    @JsonAlias("ipAddress")
    private String ipAddress;

    @JsonProperty("city")
    private String city;

    @JsonProperty("site_token")
    @JsonAlias("siteToken")
    private String siteToken;

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    public String getWorkspaceCode() {
        return workspaceCode;
    }

    public void setWorkspaceCode(String workspaceCode) {
        this.workspaceCode = workspaceCode;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getHostname() {
        return hostname;
    }

    public void setHostname(String hostname) {
        this.hostname = hostname;
    }

    public String getMacAddress() {
        return macAddress;
    }

    public void setMacAddress(String macAddress) {
        this.macAddress = macAddress;
    }

    public LocalDateTime getCaptureTime() {
        return captureTime;
    }

    public void setCaptureTime(LocalDateTime captureTime) {
        this.captureTime = captureTime;
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

    public String getDeviceToken() {
        return deviceToken;
    }

    public void setDeviceToken(String deviceToken) {
        this.deviceToken = deviceToken;
    }

    public String getCaptureType() {
        return captureType;
    }

    public void setCaptureType(String captureType) {
        this.captureType = captureType;
    }

    public String getCapturedAtUtc() {
        return capturedAtUtc;
    }

    public void setCapturedAtUtc(String capturedAtUtc) {
        this.capturedAtUtc = capturedAtUtc;
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

    public String getSiteToken() {
        return siteToken;
    }

    public void setSiteToken(String siteToken) {
        this.siteToken = siteToken;
    }
}
