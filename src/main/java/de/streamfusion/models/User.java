package de.streamfusion.models;

import jakarta.persistence.*;

import java.util.Set;

@Entity
@Table(name = "users")
public class User {
    @Id
    @Column(name = "user_id")
    private Long id;
    private String username;
    private String email;
    private String firstName;
    private String lastName;
    private Long dateOfBirth;
    private String password;
    @OneToMany(mappedBy = "user")
    private Set<Video> videos;

    @PrePersist
    public void prePersist() {
        this.id = System.currentTimeMillis();
    }
    
    public Long getID() {
        return id;
    }

    public String getUsername() {
        return this.username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return this.email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getFirstName() {
        return this.firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return this.lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public Long getDateOfBirth() {
        return this.dateOfBirth;
    }

    public void setDateOfBirth(Long dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Set<Video> getVideos() {
        return videos;
    }

    public void addVideo(Video video) {
        this.videos.add(video);
    }

    public boolean checkPassword(String password) {
        return this.password.equals(password);
    }
}
