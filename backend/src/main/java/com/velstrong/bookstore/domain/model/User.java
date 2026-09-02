package com.velstrong.bookstore.domain.model;

import com.velstrong.bookstore.domain.exception.InvalidOperationException;
import com.velstrong.bookstore.domain.model.enums.auth.RoleType;
import com.velstrong.bookstore.domain.model.enums.user.UserStatus;

import java.time.LocalDate;
import java.util.List;

public class User {

    private final Long id;
    private String username;
    private String password;
    private String email;
    private String fullname;
    private String phone;
    private LocalDate birthday;
    private String iamId;
    private Byte customerTierId;
    private UserStatus status;
    private List<String> roles;
    private List<String> scopes;

    private User(Long id, String username, String password, String email, String fullname,
                 String phone, LocalDate birthday, String iamId, Byte customerTierId,
                 UserStatus status, List<String> roles, List<String> scopes) {
        this.id = id;
        this.username = username;
        this.password = password;
        this.email = email;
        this.fullname = fullname;
        this.phone = phone;
        this.birthday = birthday;
        this.iamId = iamId;
        this.customerTierId = customerTierId;
        this.status = status;
        this.roles = roles;
        this.scopes = scopes;
    }

    public static User create(String username, String encodedPassword, String email, String fullname) {
        if (username == null || username.isBlank()) throw new InvalidOperationException("Username is required");
        if (email == null || email.isBlank()) throw new InvalidOperationException("Email is required");
        return new User(null, username, encodedPassword, email, fullname,
                null, null, null, (byte) 1, UserStatus.ACTIVE,
                List.of(RoleType.CUSTOMER.name()), List.of());
    }

    public static User reconstitute(Long id, String username, String password, String email,
                                    String fullname, String phone, LocalDate birthday, String iamId,
                                    Byte customerTierId, UserStatus status,
                                    List<String> roles, List<String> scopes) {
        return new User(id, username, password, email, fullname, phone, birthday,
                iamId, customerTierId, status, roles, scopes);
    }

    public void updateProfile(String fullname, String phone, LocalDate birthday) {
        this.fullname = fullname;
        this.phone = phone;
        this.birthday = birthday;
    }

    public void changePassword(String encodedPassword) {
        if (encodedPassword == null || encodedPassword.isBlank())
            throw new InvalidOperationException("Password cannot be blank");
        this.password = encodedPassword;
    }

    public void activate() { this.status = UserStatus.ACTIVE; }
    public void ban() { this.status = UserStatus.BANNED; }
    public void markPendingVerification() { this.status = UserStatus.PENDING_VERIFICATION; }
    public void verifyEmail() { this.status = UserStatus.ACTIVE; }
    public boolean isActive() { return status != null && status.isActive(); }

    public Long getId() { return id; }
    public String getUsername() { return username; }
    public String getPassword() { return password; }
    public String getEmail() { return email; }
    public String getFullname() { return fullname; }
    public String getPhone() { return phone; }
    public LocalDate getBirthday() { return birthday; }
    public String getIamId() { return iamId; }
    public Byte getCustomerTierId() { return customerTierId; }
    public UserStatus getStatus() { return status; }
    public List<String> getRoles() { return roles; }
    public List<String> getScopes() { return scopes; }
    public void setIamId(String iamId) { this.iamId = iamId; }
    public void setRoles(List<String> roles) { this.roles = roles; }
    public void setScopes(List<String> scopes) { this.scopes = scopes; }
}
