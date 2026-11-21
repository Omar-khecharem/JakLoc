package tn.jakloc.model;

import java.time.LocalDateTime;

/**
 * Shared user entity for the whole JakLoc application.
 *
 * <p>This class is used by authentication now and by future modules later.
 * It must never be duplicated in another package.</p>
 */
public class User {

    private long id;
    private String fullName;
    private String email;
    private String passwordHash;
    private String phone;
    private Role role;
    private LocalDateTime createdAt;

    public User() {
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    /**
     * Returns the BCrypt hash. The raw password is never stored on this object.
     */
    public String getPasswordHash() {
        return passwordHash;
    }

    public void setPasswordHash(String passwordHash) {
        this.passwordHash = passwordHash;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
