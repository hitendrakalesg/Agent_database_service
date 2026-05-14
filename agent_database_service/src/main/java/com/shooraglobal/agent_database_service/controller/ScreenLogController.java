package com.shooraglobal.agent_database_service.controller;

import com.shooraglobal.agent_database_service.dto.DeviceResponseDto;
import com.shooraglobal.agent_database_service.dto.ScreenLogRequestDto;
import com.shooraglobal.agent_database_service.dto.ScreenLogResponseDto;
import com.shooraglobal.agent_database_service.service.ScreenLogService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/screenlogs")
public class ScreenLogController {


    private final ScreenLogService screenLogService;

    public ScreenLogController(ScreenLogService screenLogService) {
        this.screenLogService = screenLogService;
    }

    @PostMapping("/upload")
    public ResponseEntity<String> uploadScreenLog(@RequestPart("data") ScreenLogRequestDto dto,
                                                  @RequestPart("file") MultipartFile file) {

        return new ResponseEntity<>(screenLogService.saveScreenLog(dto,file),HttpStatus.CREATED);
    }


    @GetMapping("/devices")
    public ResponseEntity<List<DeviceResponseDto>> getAllDevices(){

        return new ResponseEntity<>(screenLogService.getAllDevices(),HttpStatus.OK);



    }
}
