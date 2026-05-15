package com.shooraglobal.agent_database_service.controller;

import com.shooraglobal.agent_database_service.dto.DeviceResponseDto;
import com.shooraglobal.agent_database_service.dto.ScreenLogRequestDto;

import com.shooraglobal.agent_database_service.dto.ScreenLogResponseDto;
import com.shooraglobal.agent_database_service.service.ScreenLogService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.core.io.Resource;

import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;




import org.springframework.web.bind.annotation.*;


import java.io.IOException;
import java.util.List;



@RestController
@RequestMapping("/api/screenlogs")
public class ScreenLogController {


    private final ScreenLogService screenLogService;

    public ScreenLogController(ScreenLogService screenLogService) {
        this.screenLogService = screenLogService;
    }


//    Post API to upload Screen Logs.

    @PostMapping("/upload")
    public ResponseEntity<String> uploadScreenLog(@RequestPart("data") ScreenLogRequestDto dto,
                                                  @RequestPart("file") MultipartFile file) {

        return new ResponseEntity<>(screenLogService.saveScreenLog(dto,file),HttpStatus.CREATED);
    }

//  Get API to get All Devices.
    @GetMapping("/devices")
    public ResponseEntity<List<DeviceResponseDto>> getAllDevices(){

        return new ResponseEntity<>(screenLogService.getAllDevices(),HttpStatus.OK);



    }



    @GetMapping("/screenshots")
    public ResponseEntity<List<ScreenLogResponseDto>>
    getScreenLogs(

            @RequestParam Long deviceId,

            @RequestParam String date

    ) {

        return ResponseEntity.ok(screenLogService.getScreenLogs(deviceId,date)
        );
    }

    @GetMapping("/devices/{deviceId}/images/{imageId}")
    public ResponseEntity<Resource> getImage(

            @PathVariable Long deviceId,

            @PathVariable Long imageId

    ) throws IOException {

        Resource resource =
                screenLogService.getImage(
                        imageId,
                        deviceId
                );

        return ResponseEntity.ok()
                .header(
                        HttpHeaders.CONTENT_DISPOSITION,
                        "inline; file=\"" +
                                resource.getFilename() + "\""
                )
                .contentType(MediaType.IMAGE_PNG)
                .body(resource);
    }






}
