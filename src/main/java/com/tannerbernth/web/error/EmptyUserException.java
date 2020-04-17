package com.tannerbernth.web.error;

public class EmptyUserException extends RuntimeException {
	
	private static final long serialVersionUID = 5861310537366287163L;

	public EmptyUserException() {
		super();
	}

	public EmptyUserException(String message) {
        super(message);
    }
}