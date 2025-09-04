package com.personalDrive.apiErrorHandler;

import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class ApiErrors {
    @ExceptionHandler(StorageException.class)
    ProblemDetail handleStorage(StorageException ex) {
        var pd = ProblemDetail.forStatus(HttpStatus.SERVICE_UNAVAILABLE);
        pd.setTitle("Storage Unavailable");
        pd.setDetail(ex.getMessage());
        return pd;
    }
}
