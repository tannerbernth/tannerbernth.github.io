package com.tannerbernth.web.error;

public class EmailAlreadyExistsException extends RuntimeException {
	
	private static final long serialVersionUID = 5861310537366287163L;

	public EmailAlreadyExistsException() {
		super();
	}

	public EmailAlreadyExistsException(String message) {
        super(message);
    }
}