package com.shooraglobal.agent_database_service.util;

import com.shooraglobal.agent_database_service.dto.ScreenLogRequestDto;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
@Component
public class FileUtil {


    @Value("${storage.path}")
    private  String BASE_DIR;


    public Path createFile(ScreenLogRequestDto dto, MultipartFile file) throws IOException {
        System.out.println("File is creating ...");
        String folderPath =
                BASE_DIR + "/" +
                        sanitize(dto.getComputerName()) + "_" +
                        sanitize(dto.getUsername()) + "/" +
                        LocalDate.now();

        Files.createDirectories(Paths.get(folderPath));

        // FILE NAME

        String fileName =
                LocalTime.now()
                        .format(DateTimeFormatter.ofPattern("HH-mm-ss"))
                        + ".png";

        Path imagePath = Paths.get(folderPath, fileName);



        // SAVE FILE

        Files.copy(
                file.getInputStream(),
                imagePath,
                StandardCopyOption.REPLACE_EXISTING
        );

        System.out.println("File is created...");

        return imagePath;
    }

    public  String sanitize(String value) {

        if (value == null) {
            return "unknown";
        }

        return value.replaceAll("[^a-zA-Z0-9-_]", "_");
    }
}
