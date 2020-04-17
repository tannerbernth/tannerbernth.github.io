package com.tannerbernth.web.error;

public class StorageException extends RuntimeException {
	
	private static final long serialVersionUID = 5861310537366287163L;

	public StorageException() {
		super();
	}

	public StorageException(String message) {
        super(message);
    }
}