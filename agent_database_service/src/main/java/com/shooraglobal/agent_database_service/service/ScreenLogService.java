package com.shooraglobal.agent_database_service.service;

import com.shooraglobal.agent_database_service.dto.ScreenLogRequestDto;
import com.shooraglobal.agent_database_service.dto.ScreenLogResponseDto;
import com.shooraglobal.agent_database_service.entity.Device;
import com.shooraglobal.agent_database_service.entity.ScreenLog;
import com.shooraglobal.agent_database_service.repo.DeviceRepo;
import com.shooraglobal.agent_database_service.repo.ScreenLogRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.*;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

@Service
public class ScreenLogService {
    private  final DeviceRepo deviceRepo;
    private final ScreenLogRepo screenLogRepo;
    @Value("${storage.path}")
    private String BASE_DIR;

    public ScreenLogService(DeviceRepo deviceRepo, ScreenLogRepo screenLogRepo) {
        this.deviceRepo = deviceRepo;
        this.screenLogRepo = screenLogRepo;

    }

    public void saveScreenLog(ScreenLogRequestDto dto, MultipartFile file) throws IOException {

        Device device = deviceRepo
                .findByMacAddress(dto.getMacAddress())
                .orElseGet(() -> {

                    Device d = new Device();

                    d.setUsername(dto.getUsername());
                    d.setComputerName(dto.getComputerName());
                    d.setMacAddress(dto.getMacAddress());

                    return deviceRepo.save(d);


                });

        String folderPath =
                BASE_DIR + "/" +
                        sanitize(dto.getComputerName()) + "_" +
                        sanitize(dto.getUsername()) + "/" +
                        LocalDate.now();

        Files.createDirectories(Paths.get(folderPath));

        // FILE NAME

        String fileName =
                LocalTime.now()
                        .format(DateTimeFormatter.ofPattern("HH-mm"))
                        + ".png";

        Path imagePath = Paths.get(folderPath, fileName);

        // SAVE FILE

        Files.copy(
                file.getInputStream(),
                imagePath,
                StandardCopyOption.REPLACE_EXISTING
        );

        // SAVE DB RECORD

        ScreenLog screenLog = new ScreenLog();

        screenLog.setDevice(device);
        screenLog.setCaptureTime(dto.getCaptureTime());
        screenLog.setImagePath(imagePath.toString());

        screenLogRepo.save(screenLog);





    }

    private String sanitize(String value) {

        if (value == null) {
            return "unknown";
        }

        return value.replaceAll("[^a-zA-Z0-9-_]", "_");
    }

    public ScreenLogResponseDto getScreenLogs(String username, String date) {


        Device device=deviceRepo.findByUsername(username);

        if(device==null) throw new RuntimeException("User not found");

        return null;








    }
}
