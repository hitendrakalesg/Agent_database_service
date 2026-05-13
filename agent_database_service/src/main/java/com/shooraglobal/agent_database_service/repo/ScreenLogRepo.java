package com.shooraglobal.agent_database_service.repo;

import com.shooraglobal.agent_database_service.entity.ScreenLog;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ScreenLogRepo
        extends JpaRepository<ScreenLog, Long> {
}