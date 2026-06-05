package com.shooraglobal.agent_database_service.entity;

import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(
        name = "devices",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = {"company_name", "mac_address"})
        }
)
public class Device {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 100)
    private String username;

    @Column(length = 150)
    private String computerName;

    @Column(name = "mac_address", length = 50, nullable = false)
    private String macAddress;

    @Column(name = "company_name", length = 150)
    private String companyName;

    @Column(length = 150)
    private String clientName;

    @Column(length = 150)
    private String employeeName;

    @Column(length = 150)
    private String registeredUserName;

    @Column(length = 100)
    private String city;

    @Column(length = 100)
    private String productKey;

    @Column(length = 500)
    private String registeredUrl;

    private LocalDateTime lastScreenShotCaptureAt;

    @OneToMany(
            mappedBy = "device",
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    private List<ScreenLog> screenLogs = new ArrayList<>();

    public Device() {
    }

    public Device(
            String username,
            String computerName,
            String macAddress,
            String companyName,
            String employeeName
    ) {
        this.username = username;
        this.computerName = computerName;
        this.macAddress = macAddress;
        this.companyName = companyName;
        this.employeeName = employeeName;
    }

    public Long getId() {
        return id;
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



    public List<ScreenLog> getScreenLogs() {
        return screenLogs;
    }

    public void setScreenLogs(List<ScreenLog> screenLogs) {
        this.screenLogs = screenLogs;
    }

    public LocalDateTime getLastScreenShotCaptureAt() {
        return lastScreenShotCaptureAt;
    }

    public void setLastScreenShotCaptureAt(LocalDateTime lastScreenShotCaptureAt) {
        this.lastScreenShotCaptureAt = lastScreenShotCaptureAt;
    }
}
