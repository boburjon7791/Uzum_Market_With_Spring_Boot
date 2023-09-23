package com.example.demo.exception;

import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;

@ControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(value = Exception.class)
    public ResponseEntity<Object> handleException(Exception e) {
        return new ResponseEntity<>("Istisno ro'y berdi: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
    }
    @ExceptionHandler(value = BadParamException.class)
    public ResponseEntity<Object> handleException(BadParamException e) {
        return new ResponseEntity<>("Istisno ro'y berdi: " + e.getMessage(), HttpStatus.BAD_REQUEST);
    }
    @ExceptionHandler(value = ForbiddenAccessException.class)
    public ResponseEntity<Object> handleException(ForbiddenAccessException e) {
        return new ResponseEntity<>("Istisno ro'y berdi: " + e.getMessage(), HttpStatus.FORBIDDEN);
    }
    @ExceptionHandler(value = NotFoundException.class)
    public ResponseEntity<Object> handleException(NotFoundException e) {
        return new ResponseEntity<>("Istisno ro'y berdi: " + e.getMessage(), HttpStatus.NOT_FOUND);
    }
}
