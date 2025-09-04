package com.personalDrive.apiErrorHandler;

public class StorageException extends RuntimeException {
    public StorageException(String msg, Throwable cause) {
        super(msg, cause);
    } 
}
