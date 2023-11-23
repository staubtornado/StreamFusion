package de.streamfusion.services;

import de.streamfusion.models.Video;
import de.streamfusion.repositories.VideoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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

    public void saveVideo(Video video) {
        this.videoRepository.save(video);
    }

    public void deleteVideo(Video video) {
        this.videoRepository.delete(video);
    }
}
