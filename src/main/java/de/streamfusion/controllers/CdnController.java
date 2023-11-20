package de.streamfusion.controllers;

import de.streamfusion.models.Video;
import de.streamfusion.services.VideoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

import java.io.FileNotFoundException;
import java.io.RandomAccessFile;
import java.nio.file.Path;
import java.nio.file.Paths;

@RestController
public class CdnController {
    private final VideoService videoService;

    @Autowired
    public CdnController(VideoService videoService) {
        this.videoService = videoService;
    }

    @GetMapping("/cdn/v")
    @ResponseBody
    public ResponseEntity<StreamingResponseBody> cdnV(
            @RequestParam(name="id") long id,
            @RequestHeader(name="Range", required = false) String range
    ) {
        Video video = this.videoService.getVideoByID(id).orElseThrow();
        Path filePath = Paths.get("./data/%d/video.%s".formatted(id, video.getFiletype()));

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
            } catch (FileNotFoundException e) {
                throw new RuntimeException(e);
            }
        };
        return new ResponseEntity<>(responseStream, responseHeaders, HttpStatus.PARTIAL_CONTENT);
    }

    @GetMapping("/cdn/v/thumbnail")
    @ResponseBody
    public ResponseEntity<byte[]> cdnP(@RequestParam(name = "id") long id) {
        String path = "./data/%d/thumbnail.jpg".formatted(id);

        byte[] image;
        try {
            image = new UrlResource(Paths.get(path).toUri()).getInputStream().readAllBytes();
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return ResponseEntity.ok().contentType(MediaType.IMAGE_JPEG).body(image);
    }
}
