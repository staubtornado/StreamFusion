/**
 * Represents a user.
 * A user has a username, id, password and email.
 * The id is unique for each user.
 */

public class User {
    private String username;
    private long id;
    private String password;
    private String email;

    /**
     * Creates a new User object.
     * @param username The username of the user.
     * @param id The id of the user.
     * @param password The password of the user.
     * @param email The email of the user.
     */
    public User(String username, long id, String password, String email) {
        this.username = username;
        this.id = id;
        this.password = password;
        this.email = email;
    }

    /**
     * Creates a new User object from a string. Used to create a user from a database result.
     * @param data The string to parse.
     *             Format: username:id:password:email
     *             Example: "testUser:123456:password123:
     * @throws IllegalArgumentException If the data is null.
     */
    public User(String data) {
        if (data == null) {
            throw new IllegalArgumentException("Parameter data cannot be null.");
        }

        String[] split = data.split(":");
        this.username = split[0];
        this.id = Long.parseLong(split[1]);
        this.password = split[2];
        this.email = split[3];
    }

    /**
     * Returns the username of the user.
     * @return The username of the user.
     */
    public String getUsername() {
        return this.username;
    }

    /**
     * Returns the id of the user.
     * @return The id of the user.
     */
    public long getId() {
        return this.id;
    }

    /**
     * Returns the password of the user.
     * @return The password of the user.
     */
    public String getPassword() {
        return this.password;
    }

    /**
     * Returns the email of the user.
     * @return The email of the user.
     */
    public String getEmail() {
        return this.email;
    }


    /**
     * Sets the username of the user.
     * @param username The new username of the user.
     */
    public void setUsername(String username) {
        this.username = username;
    }

    /**
     * Sets the id of the user.
     * @param id The new id of the user.
     */
    public void setId(long id) {
        this.id = id;
    }

    /**
     * Sets the password of the user.
     * @param password The new password of the user.
     */
    public void setPassword(String password) {
        this.password = password;
    }

    /**
     * Sets the email of the user.
     * @param email The new email of the user.
     */
    public void setEmail(String email) {
        this.email = email;
    }

    @Override
    public String toString() {
        return String.format("%s:%d:%s:%s", this.username, this.id, this.password, this.email);
    }
}
