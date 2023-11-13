package de.streamfusion.models;

import jakarta.persistence.*;

import java.util.Set;

@Entity
@Table(name = "comments")
public class Comment {
    @Id
    private Long id;
    private String content;
    private int likes;
    private int dislikes;
    @OneToMany
    private Set<Comment> comments;
    @OneToOne
    @Column(name = "user_id")
    private User user;

    public Comment() {
        this.id = System.currentTimeMillis();
    }

    public Long getId() {
        return this.id;
    }

    public User getUser() {
        return this.user;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public int getLikes() {
        return likes;
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
}
