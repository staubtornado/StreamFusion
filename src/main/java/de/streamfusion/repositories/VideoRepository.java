package de.streamfusion.repositories;

import de.streamfusion.models.Video;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface VideoRepository extends JpaRepository<Video, Long> {
    Optional<Video> findByTitle(String title);
    Optional<Video> findByUploader(Long uploaderID);
}
