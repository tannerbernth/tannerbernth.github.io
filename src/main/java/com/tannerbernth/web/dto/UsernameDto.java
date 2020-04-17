package com.tannerbernth.web.dto;

public class UsernameDto {
	String username;
	String email;

	public UsernameDto() {
		super();
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getUsername() {
		return username;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getEmail() {
		return email;
	}
}