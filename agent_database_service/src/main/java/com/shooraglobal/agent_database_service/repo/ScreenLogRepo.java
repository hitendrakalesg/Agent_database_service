package com.shooraglobal.agent_database_service.repo;

import com.shooraglobal.agent_database_service.entity.ScreenLog;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;


public interface ScreenLogRepo
        extends JpaRepository<ScreenLog, Long> {

    List<ScreenLog> findByDeviceIdAndCaptureTimeBetween(
            Long deviceId,
            LocalDateTime start,
            LocalDateTime end
    );

    Optional<ScreenLog> findByIdAndDeviceId(
            Long id,
            Long deviceId
    );
}