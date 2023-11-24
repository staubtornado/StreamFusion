package de.streamfusion.controllers;

import de.streamfusion.exceptions.EmailAlreadyExistsException;
import de.streamfusion.exceptions.NoValidEmailException;
import de.streamfusion.exceptions.UsernameTakenException;
import de.streamfusion.models.User;
import de.streamfusion.models.Video;
import de.streamfusion.services.UserService;
import de.streamfusion.services.VideoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.NoSuchElementException;

@RestController
@RequestMapping("/api/v1")
public class APIController {
    private final UserService userService;
    private final VideoService videoService;

    @Autowired
    public APIController(UserService userService, VideoService videoService) {
        this.userService = userService;
        this.videoService = videoService;
    }

    @PostMapping(value = "/video/upload")
    public ResponseEntity<String> uploadVideo(
        @RequestParam MultipartFile file,
        @RequestParam String title,
        @RequestParam String description,
        @RequestParam long userID
    ) {
        if (file.isEmpty()) {
            return new ResponseEntity<>("File is empty.", HttpStatus.BAD_REQUEST);
        }
        if (title.isEmpty()) {
            return new ResponseEntity<>("Title is empty.", HttpStatus.BAD_REQUEST);
        }
        if (description.isEmpty()) {
            return new ResponseEntity<>("Description is empty.", HttpStatus.BAD_REQUEST);
        }

        final User user;
        try {
            user = this.userService.getUserByID(userID).orElseThrow();
        } catch (NoSuchElementException e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        try {
            this.videoService.saveVideo(file, title, description, user);
        } catch (IOException e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<>("Video successfully uploaded.", HttpStatus.OK);
    }

    @DeleteMapping(value = "/video/delete")
    public ResponseEntity<String> deleteVideo(
        @RequestParam long id,
        @RequestParam long userID
    ) {
        final Video video;
        try {
            video = this.videoService.getVideoByID(id).orElseThrow();
        } catch (NoSuchElementException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        if (video.getUser().getID() != userID) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        try {
            this.videoService.deleteVideo(video);
        } catch (IOException e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return new ResponseEntity<>("Video successfully deleted.", HttpStatus.OK);
    }
}
