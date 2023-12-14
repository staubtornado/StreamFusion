package de.streamfusion.models;

import jakarta.persistence.*;

import java.util.Set;

@Entity
@Table(name = "comments")
public class Comment {
    @Id
    @Column(name = "comment_id")
    private Long id;
    @Column(nullable = false)
    private String content;
    private int likes;
    private int dislikes;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "video_id")
    private Video video;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_id")
    private Comment parent;
    @OneToMany(mappedBy = "parent", cascade = CascadeType.ALL)
    private Set<Comment> replies;


    public Comment() {
    }

    public Comment(String content, User user, Video video) {
        this.content = content;
        this.user = user;
        this.video = video;
    }

    @PrePersist
    public void prePersist() {
        this.id = System.currentTimeMillis();
    }

    public Long getID() {
        return this.id;
    }

    public String getContent() {
        return this.content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public int getLikes() {
        return this.likes;
    }

    public void setLikes(int likes) {
        this.likes = likes;
    }

    public int getDislikes() {
        return dislikes;
    }

    public void setDislikes(int dislikes) {
        this.dislikes = dislikes;
    }

    public User getUser() {
        return user;
    }

    public Video getVideo() {
        return video;
    }

    public Comment getParent() {
        return parent;
    }

    public void setParent(Comment parent) {
        this.parent = parent;
    }

    public Set<Comment> getReplies() {
        return replies;
    }

    public void addReply(Comment reply) {
        this.replies.add(reply);
    }
}