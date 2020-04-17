package com.tannerbernth.web.error;

public class ArticleDoesNotExistException extends RuntimeException {
	
	private static final long serialVersionUID = 5861310537366287163L;

	public ArticleDoesNotExistException() {
		super();
	}

	public ArticleDoesNotExistException(String message) {
        super(message);
    }
}