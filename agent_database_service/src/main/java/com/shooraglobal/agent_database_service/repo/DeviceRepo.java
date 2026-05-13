package com.shooraglobal.agent_database_service.repo;

import com.shooraglobal.agent_database_service.entity.Device;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface DeviceRepo
        extends JpaRepository<Device, Long> {

    Optional<Device> findByMacAddress(String macAddress);
    Device findByUsername(String username);
}