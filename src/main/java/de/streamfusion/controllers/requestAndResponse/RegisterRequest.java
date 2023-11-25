package de.streamfusion.controllers.requestAndResponse;

public class RegisterRequest {
    private String username;
    private String email;
    private String firstname;
    private String lastname;
    private Long dateOfBirth;
    private String password;

    public RegisterRequest() {
    }

    public RegisterRequest(String username, String email, String firstname, String lastName, Long dateOfBirth, String password) {
        this.username = username;
        this.email = email;
        this.firstname = firstname;
        this.lastname = lastName;
        this.dateOfBirth = dateOfBirth;
        this.password = password;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getFirstname() {
        return firstname;
    }

    public void setFirstname(String firstname) {
        this.firstname = firstname;
    }

    public String getLastname() {
        return lastname;
    }

    public void setLastname(String lastname) {
        this.lastname = lastname;
    }

    public Long getDateOfBirth() {
        return dateOfBirth;
    }

    public void setDateOfBirth(Long dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}