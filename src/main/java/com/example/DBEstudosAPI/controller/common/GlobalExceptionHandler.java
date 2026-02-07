package com.example.DBEstudosAPI.controller.common;

import com.example.DBEstudosAPI.exceptions.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Set;
import java.util.stream.Collectors;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(RegistroNaoEncontradoException.class)
    public ResponseEntity<RestMenssagemErro> handleRegistroNaoEncontrado(RegistroNaoEncontradoException e){
        RestMenssagemErro restMenssagemErro = new RestMenssagemErro(HttpStatus.NOT_FOUND, e.getMessage(), Set.of());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(restMenssagemErro);
    }

    @ExceptionHandler(CategoriaNaoEncontradaException.class)
    public ResponseEntity<RestMenssagemErro> handleCategoriaNaoEncontradaException(CategoriaNaoEncontradaException e){
        RestMenssagemErro restMenssagemErro = new RestMenssagemErro(HttpStatus.NOT_FOUND, e.getMessage(), Set.of());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(restMenssagemErro);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<RestMenssagemErro> handleMethodArgumentNotValidException(MethodArgumentNotValidException e){
        Set<RestCampoErro> erros = e.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(fe -> new RestCampoErro(fe.getField(), fe.getDefaultMessage()))
                .collect(Collectors.toSet());

        RestMenssagemErro restMenssagemErro = new RestMenssagemErro(
                HttpStatus.BAD_REQUEST, "Erro de validação", erros);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(restMenssagemErro);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<RestMenssagemErro> handleHttpMessageNotReadableException(HttpMessageNotReadableException e){
        RestMenssagemErro restMenssagemErro = new RestMenssagemErro(HttpStatus.BAD_REQUEST,
                "JSON inválido ou campo com formato incorreto",
                Set.of());
        return ResponseEntity.badRequest().body(restMenssagemErro);
    }

    @ExceptionHandler(CategoriaNaoPermitidaException.class)
    public ResponseEntity<RestMenssagemErro> handleCategoriaNaoPermitidaException(CategoriaNaoPermitidaException e){
        RestMenssagemErro restMenssagemErro = new RestMenssagemErro(HttpStatus.UNPROCESSABLE_ENTITY,
                e.getMessage(),
                Set.of());
        return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).body(restMenssagemErro);
    }

    @ExceptionHandler(CategoriaEmUsoException.class)
    public ResponseEntity<RestMenssagemErro> handleCategoriaEmUsoException(CategoriaEmUsoException e){
        RestMenssagemErro restMenssagemErro = new RestMenssagemErro(HttpStatus.CONFLICT,
                e.getMessage(),
                Set.of());
        return ResponseEntity.status(HttpStatus.CONFLICT).body(restMenssagemErro);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<RestMenssagemErro> handleIllegalArgumentException(IllegalArgumentException e){
        RestMenssagemErro restMenssagemErro = new RestMenssagemErro(HttpStatus.BAD_REQUEST,
                e.getMessage(),
                Set.of());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(restMenssagemErro);
    }

    @ExceptionHandler(LoginCadastradoException.class)
    public ResponseEntity<RestMenssagemErro> handleUsuariosDuplicadosException(LoginCadastradoException e){
        RestMenssagemErro restMenssagemErro = new RestMenssagemErro(HttpStatus.CONFLICT,
                e.getMessage(),
                Set.of());
        return ResponseEntity.status(HttpStatus.CONFLICT).body(restMenssagemErro);
    }

    @ExceptionHandler(EmailCadastradoException.class)
    public ResponseEntity<RestMenssagemErro> handleUsuariosDuplicadosException(EmailCadastradoException e){
        RestMenssagemErro restMenssagemErro = new RestMenssagemErro(HttpStatus.CONFLICT,
                e.getMessage(),
                Set.of());
        return ResponseEntity.status(HttpStatus.CONFLICT).body(restMenssagemErro);
    }

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<RestMenssagemErro> handleBadCredentialsException(BadCredentialsException e){
        RestMenssagemErro restMenssagemErro = new RestMenssagemErro(HttpStatus.UNAUTHORIZED,
                e.getMessage(),
                Set.of());
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(restMenssagemErro);
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<RestMenssagemErro> handleErrosNaoTratados(RuntimeException e){
        RestMenssagemErro restMenssagemErro = new RestMenssagemErro(HttpStatus.INTERNAL_SERVER_ERROR,
                "Ocorreu um erro inesperado.",
                Set.of());
        return ResponseEntity.internalServerError().body(restMenssagemErro);
    }
}
