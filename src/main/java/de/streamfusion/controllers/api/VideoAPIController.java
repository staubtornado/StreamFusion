package de.streamfusion.controllers.api;

import de.streamfusion.controllers.requestAndResponse.EditVideoRequest;
import de.streamfusion.exceptions.VideoAlreadyRatedException;
import de.streamfusion.exceptions.VideoNotRatedException;
import de.streamfusion.models.Video;
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
            @RequestParam(value = "thumbnail") String thumbnail,
            @RequestParam(value = "imgType") String imgType,
            @RequestHeader("Cookie") String cookies
    ) {
        final Video video;
        try {
            video = this.videoService.newVideo(file, title, description, thumbnail, imgType, cookies);
        } catch (IOException e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        } catch (NoSuchElementException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(video.getID().toString(), HttpStatus.OK);
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

    @PutMapping(value = "/add-like")
    public ResponseEntity<String> addLike(
            @NonNull @RequestBody long id,
            @RequestHeader("Cookie") String cookies
    ) {
        try {
            this.videoService.addLike(id, cookies);
        } catch (VideoAlreadyRatedException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<>("Liked video successfully", HttpStatus.OK);
    }

    @PutMapping(value = "/remove-like")
    public ResponseEntity<String> removeLike(
            @NonNull @RequestBody long id,
            @RequestHeader("Cookie") String cookies
    ) {
        try {
            this.videoService.removeLike(id, cookies);
        } catch (VideoNotRatedException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<>("Removed like from video successfully", HttpStatus.OK);
    }

    @PutMapping(value = "/add-dislike")
    public ResponseEntity<String> addDislike(
            @NonNull @RequestBody long id,
            @RequestHeader("Cookie") String cookies
    ) {
        try {
            this.videoService.addDislike(id, cookies);
        } catch (VideoAlreadyRatedException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<>("Disliked video successfully", HttpStatus.OK);
    }

    @PutMapping(value = "/remove-dislike")
    public ResponseEntity<String> removeDislike(
            @NonNull @RequestBody long id,
            @RequestHeader("Cookie") String cookies
    ) {
        try {
            this.videoService.removeDislike(id, cookies);
        } catch (VideoNotRatedException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<>("Removed dislike from video successfully", HttpStatus.OK);
    }
}
