package com.personalDrive.apiErrorHandler;

public class NotFoundException extends RuntimeException {
    public NotFoundException(String msg, Throwable cause) {
        super(msg, cause);
    }

}
