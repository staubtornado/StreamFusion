package de.streamfusion.services;

import de.streamfusion.models.Video;
import de.streamfusion.repositories.VideoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;

@Service
public class SearchService {
    private final VideoRepository videoRepository;

    @Autowired
    public SearchService(VideoRepository videoRepository) {
        this.videoRepository = videoRepository;
    }

    public ArrayList<Video> searchVideo(String searchQuery) {
        ArrayList<Video> videos = new ArrayList<>();
        try {
            long videoId = Long.parseLong(searchQuery);
            videos.add(videoRepository.findById(videoId).orElseThrow());
        } catch (NumberFormatException e) {
            videos.addAll(videoRepository.findVideosByTitle(searchQuery));
        }
        return videos;
    }

}
