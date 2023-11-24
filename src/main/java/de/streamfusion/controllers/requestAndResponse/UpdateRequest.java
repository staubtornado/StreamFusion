package de.streamfusion.controllers.requestAndResponse;

public class UpdateRequest {
    private String newUsername;
    private String newEmail;
    private String newFirstname;
    private String newLastname;
    private Long newDate;
    private String newPassword;

    public UpdateRequest() {
    }

    public UpdateRequest(String newUsername, String newEmail, String newFirstname, String newLastname, Long newDate, String newPassword) {
        this.newUsername = newUsername;
        this.newEmail = newEmail;
        this.newFirstname = newFirstname;
        this.newLastname = newLastname;
        this.newDate = newDate;
        this.newPassword = newPassword;
    }

    public String getNewUsername() {
        return newUsername;
    }

    public void setNewUsername(String newUsername) {
        this.newUsername = newUsername;
    }

    public String getNewEmail() {
        return newEmail;
    }

    public void setNewEmail(String newEmail) {
        this.newEmail = newEmail;
    }

    public String getNewFirstname() {
        return newFirstname;
    }

    public void setNewFirstname(String newFirstname) {
        this.newFirstname = newFirstname;
    }

    public String getNewLastname() {
        return newLastname;
    }

    public void setNewLastname(String newLastname) {
        this.newLastname = newLastname;
    }

    public Long getNewDate() {
        return newDate;
    }

    public void setNewDate(Long newDate) {
        this.newDate = newDate;
    }

    public String getNewPassword() {
        return newPassword;
    }

    public void setNewPassword(String newPassword) {
        this.newPassword = newPassword;
    }
}
