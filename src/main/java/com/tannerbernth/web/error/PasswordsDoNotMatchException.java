package com.tannerbernth.web.error;

public class PasswordsDoNotMatchException extends RuntimeException {
	
	private static final long serialVersionUID = 5861310537366287163L;

	public PasswordsDoNotMatchException() {
		super();
	}

	public PasswordsDoNotMatchException(String message) {
        super(message);
    }
}