package de.streamfusion.controllers;

import de.streamfusion.models.Video;
import de.streamfusion.services.VideoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.NonNull;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.file.Path;
import java.nio.file.Paths;

@RestController
@RequestMapping("/cdn")
public class CDNController {
    private final VideoService videoService;

    @Autowired
    public CDNController(VideoService videoService) {
        this.videoService = videoService;
    }

    @GetMapping("/v")
    public ResponseEntity<StreamingResponseBody> cdnV(
            @RequestParam(name = "id") long id,
            @RequestHeader(name = "Range", required = false) String range
    ) {
        Video video = this.videoService.getVideoByID(id);
        Path filePath = Paths.get("./data/videos/%d/video.%s".formatted(id, video.getFiletype()));

        StreamingResponseBody responseStream;
        long fileSize = filePath.toFile().length();
        byte[] buffer = new byte[1024];
        final HttpHeaders responseHeaders = new HttpHeaders();

        if (range == null) {
            responseHeaders.add("Content-Type", "video/%s".formatted(video.getFiletype()));
            responseHeaders.add("Content-Length", String.valueOf(fileSize));

            responseStream = outputStream -> {
                try (RandomAccessFile file = new RandomAccessFile(filePath.toFile(), "r")) {
                    long pos = 0;
                    while (pos < fileSize) {
                        int read = file.read(buffer);
                        outputStream.write(buffer, 0, read);
                        pos += read;
                    }
                    outputStream.flush();
                } catch (FileNotFoundException e) {
                    throw new RuntimeException(e);
                }
            };
            return new ResponseEntity<>(responseStream, responseHeaders, HttpStatus.OK);
        }

        String[] ranges = range.replace("bytes=", "").split("-");
        long start = Long.parseLong(ranges[0]);
        long end;
        if (ranges.length > 1) {
            end = Long.parseLong(ranges[1]);
        } else {
            end = fileSize - 1;
        }

        long contentLength = end - start + 1;
        responseHeaders.add("Content-Type", "video/%s".formatted(video.getFiletype()));
        responseHeaders.add("Content-Length", String.valueOf(contentLength));
        responseHeaders.add("Content-Range", "bytes %d-%d/%d".formatted(start, end, fileSize));
        responseHeaders.add("Accept-Ranges", "bytes");

        responseStream = outputStream -> {
            try (RandomAccessFile file = new RandomAccessFile(filePath.toFile(), "r")) {
                file.seek(start);
                long pos = start;
                while (pos < end) {
                    int read = file.read(buffer);
                    outputStream.write(buffer, 0, read);
                    pos += read;
                }
                outputStream.flush();
            } catch (IOException ignored) {}
        };
        return new ResponseEntity<>(responseStream, responseHeaders, HttpStatus.PARTIAL_CONTENT);
    }

    @GetMapping("/v/thumbnail")
    public ResponseEntity<byte[]> cdnThumbnail(@RequestParam(name = "id") long id) {
        String path = "./data/videos/%d/thumbnail.%s)".formatted(id, videoService.getVideoByID(id).getThumbnailFileType());
        return getResponseEntity(path, false);
    }

    @GetMapping(value = "/u/picture", produces = {"image/png", "image/jpg", "image/jpeg"})
    public ResponseEntity<byte[]> cdnUserPicture(@RequestParam(name = "id") long id) {
        String path = "./data/user/%d/profile-picture.png".formatted(id);
        return getResponseEntity(path, false);
    }

    @GetMapping(value = "/u/banner", produces = {"image/png", "image/jpg", "image/jpeg"})
    public ResponseEntity<byte[]> cdnUserBanner(@RequestParam(name = "id") long id) {
        String path = "./data/user/%d/banner.png".formatted(id);
        return getResponseEntity(path, false);
    }

    @GetMapping(value = "/profile-picture/generate", produces = {"image/jpg", "image/jpeg"})
    public ResponseEntity<byte[]> cdnGenerateProfilePicture(
            @NonNull @RequestParam(name = "first-name") String firstName,
            @NonNull @RequestParam(name = "last-name") String lastName
    ) {
        String path = "https://ui-avatars.com/api/?name=%s+%s&size=100&background=random".formatted(firstName, lastName);
        return getResponseEntity(path, true);
    }

    private @NonNull ResponseEntity<byte[]> getResponseEntity(String path, boolean isOnline) {
        byte[] image;
        try {
            if (isOnline) {
                image = new UrlResource(path).getInputStream().readAllBytes();
            } else {
                image = new UrlResource(Paths.get(path).toUri()).getInputStream().readAllBytes();
            }
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(image, HttpStatus.OK);
    }
}
