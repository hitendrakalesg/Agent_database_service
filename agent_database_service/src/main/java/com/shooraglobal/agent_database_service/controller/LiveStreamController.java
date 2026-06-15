package com.shooraglobal.agent_database_service.controller;

import com.shooraglobal.agent_database_service.dto.LiveStreamUploadResponseDto;
import com.shooraglobal.agent_database_service.service.LiveStreamService;
import org.springframework.core.io.Resource;
import org.springframework.http.CacheControl;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.util.MultiValueMap;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;
import org.springframework.web.util.UriUtils;

import java.nio.charset.StandardCharsets;
import java.time.Duration;

@RestController
@RequestMapping("/api/livestream")
public class LiveStreamController {

    private static final String MJPEG_MEDIA_TYPE = "multipart/x-mixed-replace; boundary="
            + LiveStreamService.MJPEG_BOUNDARY;

    private final LiveStreamService liveStreamService;

    public LiveStreamController(LiveStreamService liveStreamService) {
        this.liveStreamService = liveStreamService;
    }

    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<LiveStreamUploadResponseDto> uploadFrame(
            @RequestParam MultiValueMap<String, String> fields,
            @RequestParam MultiValueMap<String, MultipartFile> files
    ) {
        return new ResponseEntity<>(liveStreamService.saveFrame(fields, resolveFrameFile(files)), HttpStatus.CREATED);
    }

    @GetMapping("/companies/{companyName}/devices/{deviceId}/latest")
    public ResponseEntity<Resource> getLatestFrame(
            @PathVariable String companyName,
            @PathVariable Long deviceId
    ) {
        LiveStreamService.LatestFrameResource latestFrame = liveStreamService.getLatestFrameResource(companyName, deviceId);
        String contentType = latestFrame.contentType() == null ? "image/jpeg" : latestFrame.contentType();

        return ResponseEntity.ok()
                .cacheControl(CacheControl.noStore())
                .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + latestFrame.fileName() + "\"")
                .contentLength(latestFrame.contentLength())
                .contentType(MediaType.parseMediaType(contentType))
                .body(latestFrame.resource());
    }

    @GetMapping(
            value = "/companies/{companyName}/devices/{deviceId}/video",
            produces = MJPEG_MEDIA_TYPE
    )
    public ResponseEntity<StreamingResponseBody> streamVideo(
            @PathVariable String companyName,
            @PathVariable Long deviceId
    ) {
        return ResponseEntity.ok()
                .cacheControl(CacheControl.noStore())
                .header(HttpHeaders.PRAGMA, "no-cache")
                .header(HttpHeaders.EXPIRES, "0")
                .header(HttpHeaders.CONTENT_TYPE, MJPEG_MEDIA_TYPE)
                .body(liveStreamService.streamVideo(companyName, deviceId));
    }

    @GetMapping(
            value = "/companies/{companyName}/devices/{deviceId}/viewer",
            produces = MediaType.TEXT_HTML_VALUE
    )
    public ResponseEntity<String> viewer(
            @PathVariable String companyName,
            @PathVariable Long deviceId
    ) {
        String encodedCompanyName = UriUtils.encodePathSegment(companyName, StandardCharsets.UTF_8);
        String videoUrl = "/api/livestream/companies/" + encodedCompanyName + "/devices/" + deviceId + "/video";
        String latestUrl = "/api/livestream/companies/" + encodedCompanyName + "/devices/" + deviceId + "/latest";
        String escapedCompanyName = escapeHtml(companyName);

        String html = """
                <!doctype html>
                <html lang="en">
                <head>
                    <meta charset="utf-8">
                    <meta name="viewport" content="width=device-width, initial-scale=1">
                    <meta http-equiv="Cache-Control" content="no-store">
                    <title>Live Screen</title>
                    <style>
                        body {
                            margin: 0;
                            min-height: 100vh;
                            background: #101418;
                            color: #f7f8f8;
                            font-family: Arial, sans-serif;
                            display: grid;
                            grid-template-rows: auto 1fr;
                        }
                        header {
                            display: flex;
                            gap: 16px;
                            align-items: center;
                            justify-content: space-between;
                            padding: 14px 18px;
                            border-bottom: 1px solid #2b333b;
                            background: #171d23;
                        }
                        h1 {
                            margin: 0;
                            font-size: 18px;
                            font-weight: 700;
                        }
                        .meta {
                            color: #bcc6cf;
                            font-size: 13px;
                        }
                        main {
                            display: grid;
                            place-items: center;
                            padding: 16px;
                        }
                        img {
                            width: min(100%, 1400px);
                            max-height: calc(100vh - 96px);
                            object-fit: contain;
                            background: #000;
                            border: 1px solid #2b333b;
                        }
                        a {
                            color: #8fd3ff;
                            text-decoration: none;
                        }
                    </style>
                </head>
                <body>
                    <header>
                        <div>
                            <h1>Live Screen</h1>
                            <div class="meta">%s / device %d</div>
                        </div>
                        <a href="%s" target="_blank" rel="noopener">Latest frame</a>
                    </header>
                    <main>
                        <img src="%s" alt="Live screen">
                    </main>
                </body>
                </html>
                """.formatted(escapedCompanyName, deviceId, latestUrl, videoUrl);

        return ResponseEntity.ok()
                .cacheControl(CacheControl.maxAge(Duration.ZERO).cachePrivate())
                .contentType(MediaType.TEXT_HTML)
                .body(html);
    }

    private String escapeHtml(String value) {
        if (value == null) {
            return "";
        }

        return value
                .replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;")
                .replace("\"", "&quot;")
                .replace("'", "&#39;");
    }

    private MultipartFile resolveFrameFile(MultiValueMap<String, MultipartFile> files) {
        if (files == null || files.isEmpty()) {
            return null;
        }

        MultipartFile file = files.getFirst("file");
        if (file != null) {
            return file;
        }

        file = files.getFirst("image");
        if (file != null) {
            return file;
        }

        return files.values()
                .stream()
                .flatMap(java.util.List::stream)
                .findFirst()
                .orElse(null);
    }
}
