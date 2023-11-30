package de.streamfusion.controllers.api;

import de.streamfusion.controllers.requestAndResponse.EditVideoRequest;
import de.streamfusion.services.VideoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.NonNull;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.NoSuchElementException;

@RestController
@RequestMapping("/api/v1/video")
public class VideoAPIController {
    private final VideoService videoService;

    @Autowired
    public VideoAPIController(VideoService videoService) {
        this.videoService = videoService;
    }

    @PostMapping(value = "/upload")
    public ResponseEntity<String> uploadVideo(
            @RequestParam(value = "file") MultipartFile file,
            @RequestParam(value = "title") String title,
            @RequestParam(value = "description") String description,
            @RequestHeader("Cookie") String cookies
    ) {
        try {
            this.videoService.newVideo(file, title, description, cookies);
        } catch (IOException e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        } catch (NoSuchElementException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>("Video successfully uploaded.", HttpStatus.OK);
    }

    @DeleteMapping(value = "/delete")
    public ResponseEntity<String> deleteVideo(@RequestBody long id, @RequestHeader("Cookie") String cookies) {
        try {
            this.videoService.deleteVideo(id, cookies);
        } catch (NoSuchElementException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } catch (IOException e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return new ResponseEntity<>("Video successfully deleted.", HttpStatus.OK);
    }

    @PostMapping(value = "/edit")
    public ResponseEntity<String> editVideo(
            @NonNull @RequestBody EditVideoRequest request,
            @RequestHeader("Cookie") String cookies
    ) {
        try {
            this.videoService.editVideo(request.title(), request.description(), request.id(), cookies);
        } catch (NoSuchElementException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>("Video successfully edited.", HttpStatus.OK);
    }

    @PutMapping(value = "/add-like")
    public ResponseEntity<String> addLike(
            @NonNull @RequestBody long id,
            @RequestHeader("Cookie") String cookies
    ) {
        this.videoService.likeVideo(id, cookies);
        return new ResponseEntity<>("Video liked successfully", HttpStatus.OK);
    }
}
