package de.streamfusion.models;

import jakarta.persistence.*;

@Entity
@Table(name = "videos")
public class Video {
    @Id
    private Long id;
    private String title;
    private int likes;
    private int dislikes;
    private int views;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    public Video() {
        this.id = System.currentTimeMillis();
    }

    public Long getId() {
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

    public Long getUploader() {
        return this.id; // TODO: Make method return proper user
    }
}
