package com.personalDrive.apiErrorHandler;

import org.springframework.dao.DataIntegrityViolationException;
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

    @ExceptionHandler(DataIntegrityViolationException.class)
    ProblemDetail onDataIntegrity(DataIntegrityViolationException ex) {
        var pd = ProblemDetail.forStatus(HttpStatus.CONFLICT);
        pd.setTitle("Data Conflict");
        pd.setDetail("A resource with the same unique fields already exists.");
        return pd;
    }

    @ExceptionHandler(NotFoundException.class)
    ProblemDetail onNotFound(NotFoundException ex) {
        var pd = ProblemDetail.forStatus(HttpStatus.NOT_FOUND);
        pd.setTitle("Data Not Found");
        pd.setDetail(ex.getMessage());
        return pd;
    }
}
