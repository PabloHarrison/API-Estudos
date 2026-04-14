package com.example.DBEstudosAPI.exceptions;

public class RefreshTokenRevogadoException extends RuntimeException {
    public RefreshTokenRevogadoException(String message) {
        super(message);
    }
}
