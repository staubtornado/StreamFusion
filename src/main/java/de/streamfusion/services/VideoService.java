package de.streamfusion.services;

import de.streamfusion.models.User;
import de.streamfusion.models.Video;
import de.streamfusion.repositories.VideoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Objects;
import java.util.Optional;

@Service
public class VideoService {
    private final VideoRepository videoRepository;

    @Autowired
    public VideoService(VideoRepository videoRepository) {
        this.videoRepository = videoRepository;
    }

    public Optional<Video> getVideoByID(long id) {
        return this.videoRepository.findById(id);
    }

    public void saveVideo(MultipartFile multipartFile, String title, String description, User user) throws IOException {
        final String fileExtension = Objects.requireNonNull(multipartFile.getContentType()).split("/")[1];
        final String[] validFileExtensions = new String[] {"mp4", "mp3", "wav", "ogg", "webm"};

        if (!Arrays.asList(validFileExtensions).contains(fileExtension)) {
            throw new IllegalArgumentException("File is not a video.");
        }

        Video video = new Video();
        video.setTitle(title);
        video.setDescription(description);
        video.setUser(user);
        video.setFiletype(fileExtension);
        this.videoRepository.save(video);

        final File onDisk = new File("%s/data/%d/video.%s".formatted(
                System.getProperty("user.dir"),
                video.getID(),
                fileExtension
        ));
        if (!onDisk.getParentFile().mkdirs()) {
            throw new IOException("Could not create directories.");
        }
        multipartFile.transferTo(onDisk);
    }

    public void deleteVideo(Video video) throws IOException {
        File directory = new File("%s/data/%d".formatted(
                System.getProperty("user.dir"),
                video.getID()
        ));
        if (!directory.exists()) {
            this.videoRepository.delete(video);
            return;
        }
        try {
            for (File file : Objects.requireNonNull(directory.listFiles())) {
                if (!file.delete()) {
                    throw new IOException("Could not delete file.");
                }
            }
        } catch (NullPointerException ignored) {}
        if (!directory.delete()) {
            throw new IOException("Could not delete directory.");
        }
        this.videoRepository.delete(video);
    }
}
