package de.streamfusion.controllers;

import de.streamfusion.models.User;
import de.streamfusion.models.Video;
import de.streamfusion.services.UserService;
import de.streamfusion.services.VideoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.io.File;
import java.io.IOException;
import java.util.Objects;

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

    @PostMapping(value = "/upload")
    public ResponseEntity<String> uploadVideo(
        @RequestParam MultipartFile file,
        @RequestParam String title,
        @RequestParam String description,
        @RequestParam long userID
    ) {
        if (file.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "File is empty.");
        }
        if (title.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Title is empty.");
        }
        if (description.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Description is empty.");
        }

        final User user = this.userService.getUserByID(userID).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "User does not exist.")
        );
        final String fileExtension = Objects.requireNonNull(file.getContentType()).split("/")[1];
        if (!fileExtension.equals("mp4")) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "File is not a video.");
        }
        Video video = new Video();
        video.setTitle(title);
        video.setDescription(description);
        video.setUser(user);
        video.setFiletype(fileExtension);
        this.videoService.saveVideo(video);

        final File videoFile = new File("%s/data/%d/video.%s".formatted(
                System.getProperty("user.dir"),
                video.getID(),
                fileExtension
        ));

        try {
            if (!videoFile.getParentFile().mkdirs()) {
                throw new IOException("Could not create parent directories.");
            }
            file.transferTo(videoFile);
        } catch (IOException e) {
            this.videoService.deleteVideo(video);
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return new ResponseEntity<>("Video successfully uploaded.", HttpStatus.OK);
    }
}
