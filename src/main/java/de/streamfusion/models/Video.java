package de.streamfusion.models;

import jakarta.persistence.*;

import java.text.DateFormat;
import java.util.Date;
import java.util.Set;
import java.util.List;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashSet;

@Entity
@Table(name = "videos")
public class Video {
    @Id
    @Column(name = "video_id")
    private Long id;
    @Column(length = 64)
    private String title;
    private int likes;
    private int dislikes;
    private int views;
    @Column(length = 4096)
    private String description;
    private String filetype;
    private String thumbnailFileType;
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

    public void addComment(Comment comment) {
        this.comments.add(comment);
    }

//    public void deleteComment(Comment comment) {
//        this.comments.remove(comment);
//    }

    public Set<Comment> getCommentsByDate(boolean desc) {
        List<Comment> commentList = new ArrayList<>(comments);
        if (desc) {
            commentList.sort(Comparator.comparing(Comment::getID, Comparator.reverseOrder()));
        } else {
            commentList.sort(Comparator.comparing(Comment::getID));
        }
        return new LinkedHashSet<>(commentList);
    }

//    public Set<Comment> getCommentsByLikes(boolean desc) {
//        ArrayList<Comment> commentList = new ArrayList<>(comments);
//        if (desc) {
//            commentList.sort(Comparator.comparing(Comment::getLikes, Comparator.reverseOrder()));
//        } else {
//            commentList.sort(Comparator.comparing(Comment::getLikes));
//        }
//        return new LinkedHashSet<>(commentList);
//    }

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

    public String getThumbnailFileType() {
        return this.thumbnailFileType;
    }

    public void setThumbnailFileType(String thumbnailFileType) {
        this.thumbnailFileType = thumbnailFileType;
    }

    public String getStreamURL() {
        return "/cdn/v?id=%d".formatted(this.id);
    }

    public String getThumbnailURL() {
        return "/cdn/v/thumbnail?id=%d".formatted(this.id);
    }

    public String getUploadDate() {
        return DateFormat.getDateInstance(DateFormat.MEDIUM).format(new Date(this.id));
    }

    public float getLikeRatio() {
        if (this.likes == 0) {
            return 0;
        }
        if (this.dislikes == 0) {
            return 1;
        }
        return (float) this.likes / (this.likes + this.dislikes);
    }
}
