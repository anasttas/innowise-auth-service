package com.kharlamova.auth_service.exception;

public class LoginNotFoundExcetion extends RuntimeException {
    public LoginNotFoundExcetion(String message) {
        super(message);
    }
}
