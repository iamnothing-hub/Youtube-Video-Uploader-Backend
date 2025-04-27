package com.youtube.video.uploader.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.youtube.video.uploader.service.YoutubeVideoUploadService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;
import java.util.Objects;

@RestController
@RequestMapping("/api/v1/video")
public class YoutubeVideoUploadController {

    private YoutubeVideoUploadService youtubeVideoUploadService;



    public YoutubeVideoUploadController(YoutubeVideoUploadService youtubeVideoUploadService) {
        this.youtubeVideoUploadService = youtubeVideoUploadService;
    }

    @PostMapping("/upload")
    @CrossOrigin(origins = "http://localhost:5173/*")
    public ResponseEntity<String> uploadVideo(
            @RequestParam("title") String title,
            @RequestParam("description") String description,
            @RequestParam("visibility") String visibility,
            @RequestParam("videoFile") MultipartFile videoFile,
            @RequestHeader("Authorization") String authorization,
            @RequestParam("tags") String[] tags
            ) throws IOException {

        System.out.println("Video is: " + videoFile);
        System.out.println("Token is: " + authorization);
        String response = youtubeVideoUploadService.uploadVideo(title, description, visibility,
                            videoFile, authorization.replace("Bearer ", ""), tags);

//        return new ResponseEntity<>(response, HttpStatus.OK);
        return ResponseEntity.ok(response);
    }

    //

//    Generate Meta Tags by AI
    @PostMapping("/generate-meta-data")
    public ResponseEntity<Map<String, Object>> generateMetaTags(
            @RequestParam("title") String title
    ) throws JsonProcessingException {

        Map<String, Object> result = youtubeVideoUploadService.generateMetaData(title);

        return ResponseEntity.ok(result);
    }

}
