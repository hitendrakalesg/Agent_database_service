package com.shooraglobal.agent_database_service.service;

import com.shooraglobal.agent_database_service.dto.DeviceResponseDto;
import com.shooraglobal.agent_database_service.dto.ScreenLogRequestDto;

import com.shooraglobal.agent_database_service.dto.ScreenLogResponseDto;
import com.shooraglobal.agent_database_service.entity.Device;
import com.shooraglobal.agent_database_service.entity.ScreenLog;
import com.shooraglobal.agent_database_service.exception.AgentDatabaseServiceException;
import com.shooraglobal.agent_database_service.repo.DeviceRepo;
import com.shooraglobal.agent_database_service.repo.ScreenLogRepo;
import com.shooraglobal.agent_database_service.util.FileUtil;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.nio.file.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;




@Service
public class ScreenLogService {
    private  final DeviceRepo deviceRepo;
    private final ScreenLogRepo screenLogRepo;
    private final FileUtil fileUtil;


    public ScreenLogService(DeviceRepo deviceRepo, ScreenLogRepo screenLogRepo, FileUtil fileUtil) {
        this.deviceRepo = deviceRepo;
        this.screenLogRepo = screenLogRepo;

        this.fileUtil = fileUtil;
    }
    @Transactional
    public String saveScreenLog(ScreenLogRequestDto dto, MultipartFile file) {

        String type = file.getContentType();

        if (type == null || !type.startsWith("image/")) {
            throw new AgentDatabaseServiceException("Wrong File Type!");
        }

        Device device = deviceRepo
                .findByMacAddress(dto.getMacAddress())
                .orElseGet(Device::new);

        device.setUsername(dto.getUsername());

        device.setComputerName(dto.getComputerName());

        device.setMacAddress(dto.getMacAddress());

        device.setLastScreenShotCaptureAt(dto.getCaptureTime());

        device = deviceRepo.save(device);



//      creating folder to store screenshot

        Path imagePath= null;
        try {
            imagePath = fileUtil.createFile(dto,file);
        } catch (IOException e) {
            throw new AgentDatabaseServiceException("Error in Creating File");
        }

        // SAVE DB RECORD

        ScreenLog screenLog = new ScreenLog();

        screenLog.setDevice(device);
        screenLog.setCaptureTime(dto.getCaptureTime());
        screenLog.setImagePath(imagePath.toString());

        screenLogRepo.save(screenLog);


        return "Screen Log Uploaded Successfully.";





    }


    public List<DeviceResponseDto> getAllDevices() {

        List<Device> devices = deviceRepo.findAll();

        return devices.stream()
                .map(device -> new DeviceResponseDto(


                        device.getId(),
                        device.getUsername(),
                        device.getComputerName(),
                        device.getMacAddress(),
                        device.getLastScreenShotCaptureAt()

                ))
                .toList();

    }

    public List<ScreenLogResponseDto> getScreenLogs( Long deviceId,String date) {

        LocalDate localDate = LocalDate.parse(date);

        LocalDateTime start = localDate.atStartOfDay();

        LocalDateTime end = localDate.plusDays(1).atStartOfDay();

        List<ScreenLog> logs =
                screenLogRepo.findByDeviceIdAndCaptureTimeBetween(
                        deviceId,
                        start,
                        end
                );

        return logs.stream()
                .map(log -> new ScreenLogResponseDto(

                        log.getId(),

                        "/api/screenlogs/image/" + log.getId(),

                        log.getCaptureTime()

                ))
                .toList();
    }

    public Resource getImage(
            Long imageId,
            Long deviceId
    ) throws IOException {

        ScreenLog screenLog =
                screenLogRepo
                        .findByIdAndDeviceId(
                                imageId,
                                deviceId
                        )
                        .orElseThrow(() ->
                                new AgentDatabaseServiceException(
                                        "Image not found"
                                )
                        );

        Path path = Paths.get(
                screenLog.getImagePath()
        );

        if (!Files.exists(path)) {

            throw new AgentDatabaseServiceException(
                    "Image file not found"
            );
        }

        return new UrlResource(path.toUri());
    }
}
