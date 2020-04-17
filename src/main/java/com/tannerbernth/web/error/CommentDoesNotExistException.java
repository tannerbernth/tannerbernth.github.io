package com.tannerbernth.web.error;

public class CommentDoesNotExistException extends RuntimeException {
	
	private static final long serialVersionUID = 5861310537366287163L;

	public CommentDoesNotExistException() {
		super();
	}

	public CommentDoesNotExistException(String message) {
        super(message);
    }
}