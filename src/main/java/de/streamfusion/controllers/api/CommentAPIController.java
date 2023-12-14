package de.streamfusion.controllers.api;

import de.streamfusion.controllers.requestAndResponse.CommentPostRequest;
import de.streamfusion.services.CommentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.NoSuchElementException;

@RestController
@RequestMapping("/api/v1/comment")
public class CommentAPIController {
    private final CommentService commentService;

    @Autowired
    public CommentAPIController(CommentService commentService) {
        this.commentService = commentService;
    }

    @PostMapping(value = "/post-comment")
    public ResponseEntity<String> postComment(
            @RequestBody CommentPostRequest commentPostRequest,
            @RequestHeader("Cookie") String cookies
    ) {
        try {
            this.commentService.postComment(
                    commentPostRequest.videoID(),
                    cookies,
                    commentPostRequest.commentContent()
            );
        } catch (NoSuchElementException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<>("Successfully commented video", HttpStatus.OK);
    }
}
