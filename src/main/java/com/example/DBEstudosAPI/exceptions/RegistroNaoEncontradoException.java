package com.example.DBEstudosAPI.exceptions;

public class RegistroNaoEncontradoException extends RuntimeException{
    public RegistroNaoEncontradoException(String msg){
        super(msg);
    }
}
