package com.tannerbernth.web.dto;

public interface PasswordDto {
	public String password = "";
	public String matchingPassword = "";

	public String getPassword();
	public void setPassword(String currentPassword);
	public String getMatchingPassword();
	public void setMatchingPassword(String matchingPassword);
}