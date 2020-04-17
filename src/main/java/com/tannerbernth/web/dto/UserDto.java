package com.tannerbernth.web.dto;

import javax.validation.constraints.Size;
import javax.validation.constraints.Email;

import com.tannerbernth.web.dto.constraint.PasswordMatches;

import com.tannerbernth.web.dto.PasswordDto;

@PasswordMatches
public class UserDto implements PasswordDto {

    @Size(max=20, message="Username cannot exceed {max} characters")
    @Size(min=1, message="Enter a username")
    private String username;

    @Size(min=8, message="Password must contain at least {min} characters")
    private String password;

    @Size(min=1, message="Re-enter your password")
    private String matchingPassword;

    @Email(regexp=".+@.+\\..+", message="Email must be valid: email@domain.com")
    private String email;

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getMatchingPassword() {
        return matchingPassword;
    }

    public void setMatchingPassword(String matchingPassword) {
        this.matchingPassword = matchingPassword;
    }
}