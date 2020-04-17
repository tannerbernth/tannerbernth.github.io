package com.tannerbernth.web.error;

public class StorageFileNotFoundException extends RuntimeException {
	
	private static final long serialVersionUID = 5861310537366287163L;

	public StorageFileNotFoundException() {
		super();
	}

	public StorageFileNotFoundException(String message) {
        super(message);
    }
}