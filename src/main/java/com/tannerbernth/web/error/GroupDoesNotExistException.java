package com.tannerbernth.web.error;

public class GroupDoesNotExistException extends RuntimeException {
	
	private static final long serialVersionUID = 5861310537366287163L;

	public GroupDoesNotExistException() {
		super();
	}

	public GroupDoesNotExistException(String message) {
        super(message);
    }
}