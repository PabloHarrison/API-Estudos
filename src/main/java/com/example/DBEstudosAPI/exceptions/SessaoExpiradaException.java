package com.example.DBEstudosAPI.exceptions;

public class SessaoExpiradaException extends RuntimeException {
    public SessaoExpiradaException(String message) {
        super(message);
    }
}
