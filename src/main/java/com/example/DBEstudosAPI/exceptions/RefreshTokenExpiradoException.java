package com.example.DBEstudosAPI.exceptions;

public class RefreshTokenExpiradoException extends RuntimeException {
    public RefreshTokenExpiradoException(String message) {
        super(message);
    }
}
