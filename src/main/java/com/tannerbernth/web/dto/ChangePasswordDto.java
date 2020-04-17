package com.tannerbernth.web.dto;

import javax.validation.constraints.Size;

import com.tannerbernth.web.dto.constraint.PasswordMatches;

import com.tannerbernth.web.dto.PasswordDto;

@PasswordMatches
public class ChangePasswordDto implements PasswordDto {
	
	@Size(min=1, message="Enter your current password")
    private String currentPassword;

    @Size(min=8, message="Password must contain at least {min} characters")
    private String password;

    @Size(min=1, message="Re-enter your password")
    private String matchingPassword;

    public String getCurrentPassword() {
        return currentPassword;
    }

    public void setCurrentPassword(String currentPassword) {
        this.currentPassword = currentPassword;
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