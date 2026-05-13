package com.shooraglobal.agent_database_service.entity;

import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(
        name = "devices",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = "macAddress")
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

    @Column(length = 50, nullable = false, unique = true)
    private String macAddress;

    private LocalDateTime createdAt = LocalDateTime.now();

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
            String macAddress
    ) {
        this.username = username;
        this.computerName = computerName;
        this.macAddress = macAddress;
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

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public List<ScreenLog> getScreenLogs() {
        return screenLogs;
    }

    public void setScreenLogs(List<ScreenLog> screenLogs) {
        this.screenLogs = screenLogs;
    }
}