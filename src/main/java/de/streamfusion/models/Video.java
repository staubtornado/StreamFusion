package de.streamfusion.models;

import jakarta.persistence.*;

import java.util.Set;

@Entity
@Table(name = "videos")
public class Video {
    @Id
    @Column(name = "video_id")
    private Long id;
    private String title;
    private int likes;
    private int dislikes;
    private int views;
    private String description;
    private String filetype;
    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;
    @OneToMany(mappedBy = "video", cascade = CascadeType.ALL)
    private Set<Comment> comments;

    @PrePersist
    public void prePersist() {
        this.id = System.currentTimeMillis();
    }

    public Long getID() {
        return this.id;
    }

    public String getTitle() {
        return this.title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getLikes() {
        return this.likes;
    }

    public void setLikes(int likes) {
        this.likes = likes;
    }

    public int getDislikes() {
        return this.dislikes;
    }

    public void setDislikes(int dislikes) {
        this.dislikes = dislikes;
    }

    public int getViews() {
        return this.views;
    }

    public void setViews(int views) {
        this.views = views;
    }

    public User getUser() {
        return this.user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Set<Comment> getComments() {
        return comments;
    }

    public String getFiletype() {
        return this.filetype;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setFiletype(String filetype) {
        this.filetype = filetype;
    }

    public String getStreamURL() {
        return "/cdn/v?id=%d".formatted(this.id);
    }

    public String getThumbnailURL() {
        return "/cdn/v/thumbnail?id=%d".formatted(this.id);
    }
}
