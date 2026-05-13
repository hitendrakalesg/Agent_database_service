package com.shooraglobal.agent_database_service.controller;

import com.shooraglobal.agent_database_service.dto.ScreenLogRequestDto;
import com.shooraglobal.agent_database_service.dto.ScreenLogResponseDto;
import com.shooraglobal.agent_database_service.service.ScreenLogService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/screenlogs")
public class ScreenLogController {


    private final ScreenLogService screenLogService;

    public ScreenLogController(ScreenLogService screenLogService) {
        this.screenLogService = screenLogService;
    }

    @PostMapping("/upload")
    public ResponseEntity<String> uploadScreenLog(
            @RequestPart("data")
            ScreenLogRequestDto dto,

            @RequestPart("file")
            MultipartFile file
    ) {

        try {

            screenLogService.saveScreenLog(dto, file);

            return ResponseEntity.ok("Uploaded");

        } catch (Exception e) {

            return ResponseEntity
                    .internalServerError()
                    .body(e.getMessage());
        }
    }


    @GetMapping("/get")
    public ResponseEntity<ScreenLogResponseDto> getScreenLogs( @RequestParam String username , @RequestParam String date){

        return new ResponseEntity<>(screenLogService.getScreenLogs(username,date),HttpStatus.OK);



    }
}
