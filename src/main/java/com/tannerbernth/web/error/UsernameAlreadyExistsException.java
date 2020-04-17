package com.tannerbernth.web.error;

public class UsernameAlreadyExistsException extends RuntimeException {
	
	private static final long serialVersionUID = 5861310537366287163L;

	public UsernameAlreadyExistsException() {
		super();
	}

	public UsernameAlreadyExistsException(String message) {
        super(message);
    }
}