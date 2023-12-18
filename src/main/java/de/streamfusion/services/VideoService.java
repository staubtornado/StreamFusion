package de.streamfusion.services;

import de.streamfusion.exceptions.VideoAlreadyRatedException;
import de.streamfusion.exceptions.VideoNotRatedException;
import de.streamfusion.models.User;
import de.streamfusion.models.Video;
import de.streamfusion.repositories.UserRepository;
import de.streamfusion.repositories.VideoRepository;
import org.apache.tomcat.util.codec.binary.Base64;
import org.apache.tomcat.util.http.fileupload.MultipartStream;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.NoSuchElementException;
import java.util.Objects;

@Service
public class VideoService {
    private final VideoRepository videoRepository;
    private final AuthenticationService authenticationService;
    private final UserRepository userRepository;

    @Autowired
    public VideoService(
            VideoRepository videoRepository,
            AuthenticationService authenticationService,
            UserRepository userRepository) {
        this.videoRepository = videoRepository;
        this.authenticationService = authenticationService;
        this.userRepository = userRepository;
    }

    /**
     * Returns the video with the given ID.
     *
     * @param id The ID of the video.
     * @return The video with the given ID.
     * @throws NoSuchElementException If the video does not exist.
     */
    public Video getVideoByID(long id) {
        return this.videoRepository.findById(id).orElseThrow();
    }

    public Video newVideo(
            @NonNull MultipartFile multipartFile,
            String title,
            String description,
            String thumbnail,
            String cookies
    ) throws IOException {
        if (title == null || title.isBlank()) {
            throw new IllegalArgumentException("Title cannot be empty.");
        }
        if (description == null || description.isBlank()) {
            throw new IllegalArgumentException("Description cannot be empty.");
        }
        if (cookies == null) {
            throw new IllegalArgumentException("No cookies provided.");
        }
        final User user = this.authenticationService.getUserFromToken(
                AuthenticationService.extractTokenFromCookie(cookies)
        );

        final String videoFileExtension = Objects.requireNonNull(multipartFile.getContentType()).split("/")[1];
        final String[] validVideoFileExtensions = new String[] {"mp4", "mp3", "wav", "ogg", "webm"};

        if (!Arrays.asList(validVideoFileExtensions).contains(videoFileExtension)) {
            throw new IllegalArgumentException("File is not a video.");
        }

        Video video = new Video();
        video.setTitle(title);
        video.setDescription(description);
        video.setUser(user);
        video.setFiletype(videoFileExtension);
        this.videoRepository.save(video);

        final File videoOnDisk = new File("%s/data/videos/%d/video.%s".formatted(
                System.getProperty("user.dir"),
                video.getID(),
                videoFileExtension
        ));
        if (!videoOnDisk.getParentFile().mkdirs()) {
            throw new IOException("Could not create directories.");
        }
        multipartFile.transferTo(videoOnDisk);


        final  String thumbnailFileExtension = thumbnail.split("\\.")[1];
        final String[] validThumbnailFileExtensions = new String[] {"xbm", "tif", "jfif", "ico", "tiff", "gif", "svg",
                "webp", "svgz", "jpg", "jpeg", "png", "bmp", "pjp", "apng", "pjpeg", "avif"};

        if (!Arrays.asList(validThumbnailFileExtensions).contains(thumbnailFileExtension)) {
            throw new MultipartStream.IllegalBoundaryException("Thumbnail is not a image");
        }

        final File thumbnailOnDisk = new File("%s/data/videos/%d/thumbnail.%s".formatted(
                System.getProperty("user.dir"),
                video.getID(),
                thumbnailFileExtension
        ));
        // Write string to file
        if (!thumbnailOnDisk.createNewFile()) {
            throw new IOException("Could not create file.");
        }
        Files.write(thumbnailOnDisk.toPath(), Base64.decodeBase64(thumbnail));

        return video;
    }

    public void deleteVideo(@NonNull long id, String cookie) throws IOException {
        final Video video = this.videoRepository.findById(id).orElseThrow();
        final User user = this.authenticationService.getUserFromToken(
                AuthenticationService.extractTokenFromCookie(cookie)
        );
        if (!Objects.equals(video.getUser().getID(), user.getID())) {
            throw new IllegalArgumentException("User does not own video.");
        }
        File directory = new File("%s/data/videos/%d".formatted(
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

    public void editVideo(String title, String description, long id, String cookies) throws IllegalArgumentException {
        final User user = this.authenticationService.getUserFromToken(
                AuthenticationService.extractTokenFromCookie(cookies)
        );
        final Video video = this.videoRepository.findById(id).orElseThrow();
        if (!Objects.equals(video.getUser().getID(), user.getID())) {
            throw new IllegalArgumentException("User does not own video.");
        }
        video.setTitle(title);
        video.setDescription(description);
        this.videoRepository.save(video);
    }

    public void addView(long id) {
        final Video video = this.videoRepository.findById(id).orElseThrow();
        video.setViews(video.getViews() + 1);
        this.videoRepository.save(video);
    }

    public void addLike(long id, String cookies) throws VideoAlreadyRatedException {
        final User user = this.authenticationService.getUserFromToken(
                AuthenticationService.extractTokenFromCookie(cookies)
        );
        final Video video = this.videoRepository.findById(id).orElseThrow();

        if (user.getDislikedVideos().contains(video)) {
            throw new VideoAlreadyRatedException("Video already disliked");
        }
        if (user.getLikedVideos().contains(video)) {
            throw new VideoAlreadyRatedException("Video already liked");
        }
        user.addLikedVideo(video);
        video.setLikes(video.getLikes() + 1);
        this.userRepository.save(user);
    }

    public void removeLike(long id, String cookies) throws VideoNotRatedException {
        final User user = this.authenticationService.getUserFromToken(
                AuthenticationService.extractTokenFromCookie(cookies)
        );
        final Video video = this.videoRepository.findById(id).orElseThrow();

        if (!user.getLikedVideos().contains(video)) {
            throw new VideoNotRatedException("Video is not liked");
        }
        user.removeLikedVideo(video);
        video.setLikes(video.getLikes() - 1);
        this.userRepository.save(user);
    }

    public void addDislike(long id, String cookies) throws VideoAlreadyRatedException {
        final User user = this.authenticationService.getUserFromToken(
                AuthenticationService.extractTokenFromCookie(cookies)
        );
        final Video video = this.videoRepository.findById(id).orElseThrow();

        if (user.getLikedVideos().contains(video)) {
            throw new VideoAlreadyRatedException("Video already liked");
        }
        if (user.getDislikedVideos().contains(video)) {
            throw new VideoAlreadyRatedException("Video already disliked");
        }
        user.addDislikedVideo(video);
        video.setDislikes(video.getDislikes() + 1);
        this.userRepository.save(user);
    }

    public void removeDislike(long id, String cookies) throws VideoNotRatedException {
        final User user = this.authenticationService.getUserFromToken(
                AuthenticationService.extractTokenFromCookie(cookies)
        );
        final Video video = this.videoRepository.findById(id).orElseThrow();

        if (!user.getDislikedVideos().contains(video)) {
            throw new VideoNotRatedException("Video is not disliked");
        }
        user.removeDislikedVideo(video);
        video.setDislikes(video.getDislikes() - 1);
        this.userRepository.save(user);
    }
}
