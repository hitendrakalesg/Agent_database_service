package com.shooraglobal.agent_database_service.repo;

import com.shooraglobal.agent_database_service.entity.Device;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface DeviceRepo
        extends JpaRepository<Device, Long> {

    Optional<Device> findByCompanyNameIgnoreCaseAndMacAddressIgnoreCase(String companyName, String macAddress);

    List<Device> findByCompanyNameIgnoreCaseOrderByEmployeeNameAscComputerNameAsc(String companyName);

    Device findByUsername(String username);
}
