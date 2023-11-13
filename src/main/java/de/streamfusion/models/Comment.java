package de.streamfusion.models;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

import java.util.Set;

@Entity
@Table(name = "comments")
public class Comment {
    @Id
    private Long id;
    private Long userId;
    private String content;
    private int likes;
    private int dislikes;
    @OneToMany
    private Set<Comment> comments;

    public Comment() {
        this.id = System.currentTimeMillis();
    }

    public Long getId() {
        return this.id;
    }

    public Long getUserId() {
        return userId;
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
