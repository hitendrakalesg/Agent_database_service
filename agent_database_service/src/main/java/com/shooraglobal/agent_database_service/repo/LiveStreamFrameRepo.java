package com.shooraglobal.agent_database_service.repo;

import com.shooraglobal.agent_database_service.entity.LiveStreamFrame;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface LiveStreamFrameRepo extends JpaRepository<LiveStreamFrame, Long> {

    Optional<LiveStreamFrame> findTopByDevice_IdAndDevice_CompanyNameIgnoreCaseOrderByCreatedAtDesc(
            Long deviceId,
            String companyName
    );
}
