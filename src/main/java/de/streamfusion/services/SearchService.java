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

    /**
     * Searches for videos in the video repository based on the given search query.
     *
     * @param searchQuery the search query
     * @return an ArrayList of Video objects that match the search query
     */
    public ArrayList<Video> searchVideo(String searchQuery) {
        ArrayList<Video> videos = new ArrayList<>();
        try {
            long videoId = Long.parseLong(searchQuery);
            videos.add(videoRepository.findById(videoId).orElseThrow());
        } catch (NumberFormatException e) {
            for (Video video : this.videoRepository.findAll()) {
                String title = video.getTitle();
                if (title.toLowerCase().contains(searchQuery.toLowerCase())) {
                    videos.add(video);
                }
                else if (fuzzyMatch(searchQuery, title)) {
                    videos.add(video);
                }
            }
        }
        return videos;
    }

    /**
     * Uses the Levenshtein Distance Algorithm to compare two Strings.
     * Returns true if the Levenshtein distance between s1 and s2 is less than or equal to 0.3 * (n + m),
     * where n and m are the lengths of s1 and s2, respectively.
     *
     * @param s1 the first string
     * @param s2 the second string
     * @return true if the Levenshtein distance between s1 and s2 is less than or equal to 0.3 * (n + m)
     */
    private static boolean fuzzyMatch(String s1, String s2) {
        int n = s1.length();
        int m = s2.length();
        if (n > m) {
            // swap the input strings to consume less memory
            String temp = s1;
            s1 = s2;
            s2 = temp;
            n = s1.length();
            m = s2.length();
        }
        int maxDistance = (int) Math.ceil(0.3 * (n + m));
        int[] currentRow = new int[n + 1];
        for (int i = 0; i <= n; i++) {
            currentRow[i] = i;
        }
        for (int j = 1; j <= m; j++) {
            int[] previousRow = currentRow;
            currentRow = new int[n + 1];
            currentRow[0] = j;
            for (int i = 1; i <= n; i++) {
                int insertCost = currentRow[i - 1] + 1;
                int deleteCost = previousRow[i] + 1;
                int replaceCost = previousRow[i - 1] + (s1.charAt(i - 1) == s2.charAt(j - 1) ? 0 : 1);
                currentRow[i] = Math.min(Math.min(insertCost, deleteCost), replaceCost);
            }
        }
        return currentRow[n] <= maxDistance;
    }
}
