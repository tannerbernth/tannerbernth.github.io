package com.tannerbernth.web.error;

public class UsernameDoesNotExistException extends RuntimeException {
	
	private static final long serialVersionUID = 5861310537366287163L;

	public UsernameDoesNotExistException() {
		super();
	}

	public UsernameDoesNotExistException(String message) {
        super(message);
    }
}