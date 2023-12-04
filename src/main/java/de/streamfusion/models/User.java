package de.streamfusion.models;

import jakarta.persistence.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;
import java.util.Set;

@Entity
@Table(name = "users")
public class User implements UserDetails {
    @Id
    @Column(name = "user_id")
    private Long id;
    private String username;
    private String email;
    private String firstName;
    private String lastName;
    private Long dateOfBirth;
    private String password;
    @Enumerated(EnumType.STRING)
    private Role role;
    @OneToMany(mappedBy = "user")
    private Set<Video> videos;
    @ManyToMany
    @JoinTable(
            name = "liked_videos",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "video_id"))
    private Set<Video> likedVideos;
    @ManyToMany
    @JoinTable(
            name = "disliked_videos",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "video_id"))
    private Set<Video> dislikedVideos;

    public User() {}

    public User(
            String username,
            String email,
            String firstname,
            String lastname,
            Long dateOfBirth,
            String password,
            Role role
    ) {
        this.username = username;
        this.email = email;
        this.firstName = firstname;
        this.lastName = lastname;
        this.dateOfBirth = dateOfBirth;
        this.password = password;
        this.role = role;
        this.videos = Set.of();
        this.likedVideos = Set.of();
        this.dislikedVideos = Set.of();
    }

    @PrePersist
    public void prePersist() {
        this.id = System.currentTimeMillis();
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority(this.role.name()));
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    public Long getID() {
        return id;
    }
    @Override
    public String getPassword() {
        return this.password;
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

    public String getProfilePictureURL() {
        return "/cdn/u/picture?id=%d".formatted(this.id);
    }

    public void addLikedVideo(Video video) {
        this.likedVideos.add(video);
    }

    public Set<Video> getLikedVideos() {
        return this.likedVideos;
    }

    public void removeLikedVideo(Video video) {
        this.likedVideos.remove(video);
    }

    public void  addDislikedVideo(Video video) {
        this.dislikedVideos.add(video);
    }

    public Set<Video> getDislikedVideos(){
        return this.dislikedVideos;
    }
    public void removeDislikedVideo(Video video) {
        this.dislikedVideos.remove(video);
    }
}
