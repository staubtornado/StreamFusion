package de.streamfusion.services;

import de.streamfusion.models.Comment;
import de.streamfusion.models.User;
import de.streamfusion.models.Video;
import de.streamfusion.repositories.CommentRepository;
import de.streamfusion.repositories.VideoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CommentService {
    private final AuthenticationService authenticationService;
    private final CommentRepository commentRepository;
    private final VideoRepository videoRepository;

    @Autowired
    public CommentService(
            AuthenticationService authenticationService,
            CommentRepository commentRepository,
            VideoRepository videoRepository
    ) {
        this.authenticationService = authenticationService;
        this.commentRepository = commentRepository;
        this.videoRepository = videoRepository;
    }

    public void postComment(long videoId, String cookies, String content) {
        final User user = this.authenticationService.getUserFromToken(
                AuthenticationService.extractTokenFromCookie(cookies)
        );
        final Video video = this.videoRepository.findById(videoId).orElseThrow();
        Comment comment = new Comment(content, user, video);

        this.commentRepository.save(comment);
        video.addComment(comment);
    }

    public void likeComment(long id) {
        Comment comment = this.commentRepository.findById(id).orElseThrow();
        comment.setLikes(comment.getLikes() + 1);
        this.commentRepository.save(comment);
    }

    public void dislikeComment(long id) {
        Comment comment = this.commentRepository.findById(id).orElseThrow();
        comment.setDislikes(comment.getDislikes() + 1);
        this.commentRepository.save(comment);
    }

    public void removeComment(long id) {
        Comment comment = this.commentRepository.findById(id).orElseThrow();
        this.commentRepository.delete(comment);
    }
}
