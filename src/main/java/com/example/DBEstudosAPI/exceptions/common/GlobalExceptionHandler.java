package com.example.DBEstudosAPI.exceptions.common;

import com.example.DBEstudosAPI.exceptions.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.InsufficientAuthenticationException;
import org.springframework.security.oauth2.server.resource.InvalidBearerTokenException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Set;
import java.util.stream.Collectors;

@RestControllerAdvice
@Slf4j
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

    @ExceptionHandler(UsuarioNaoEncontradoException.class)
    public ResponseEntity<RestMenssagemErro> handleUsuarioNaoEncontradoException(UsuarioNaoEncontradoException e){
        RestMenssagemErro restMenssagemErro = new RestMenssagemErro(
                HttpStatus.NOT_FOUND,
                e.getMessage(),
                Set.of());
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
        log.warn("event=validation_failed status=400 error_count={} fields={} message=invalid_request_data",
                erros.size(),
                erros);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(restMenssagemErro);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<RestMenssagemErro> handleHttpMessageNotReadableException(HttpMessageNotReadableException e){
        RestMenssagemErro restMenssagemErro = new RestMenssagemErro(HttpStatus.BAD_REQUEST,
                "JSON inválido ou campo com formato incorreto",
                Set.of());
        log.warn("event=request_malformed status=400 error={} message=invalid_request_body", e.getMessage());
        return ResponseEntity.badRequest().body(restMenssagemErro);
    }

    @ExceptionHandler(CategoriaNaoPermitidaException.class)
    public ResponseEntity<RestMenssagemErro> handleCategoriaNaoPermitidaException(CategoriaNaoPermitidaException e){
        RestMenssagemErro restMenssagemErro = new RestMenssagemErro(HttpStatus.BAD_REQUEST,
                e.getMessage(),
                Set.of());
        log.warn("event=business_rule_violation message=category_not_allowed detail={}", e.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(restMenssagemErro);
    }

    @ExceptionHandler(CategoriaEmUsoException.class)
    public ResponseEntity<RestMenssagemErro> handleCategoriaEmUsoException(CategoriaEmUsoException e){
        RestMenssagemErro restMenssagemErro = new RestMenssagemErro(HttpStatus.CONFLICT,
                e.getMessage(),
                Set.of());
        log.warn("event=category_in_use status=409 message=resource_in_use detail={}", e.getMessage());
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
    public ResponseEntity<RestMenssagemErro> handleLoginCadastradoException(LoginCadastradoException e){
        RestMenssagemErro restMenssagemErro = new RestMenssagemErro(HttpStatus.CONFLICT,
                e.getMessage(),
                Set.of());
        log.warn("event=duplicate_registration_by_login status=409 detail={}", e.getMessage());
        return ResponseEntity.status(HttpStatus.CONFLICT).body(restMenssagemErro);
    }

    @ExceptionHandler(EmailCadastradoException.class)
    public ResponseEntity<RestMenssagemErro> handleEmailCadastradoException(EmailCadastradoException e){
        RestMenssagemErro restMenssagemErro = new RestMenssagemErro(HttpStatus.CONFLICT,
                e.getMessage(),
                Set.of());
        log.warn("event=duplicate_registration_by_email status=409 detail={}", e.getMessage());
        return ResponseEntity.status(HttpStatus.CONFLICT).body(restMenssagemErro);
    }

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<RestMenssagemErro> handleBadCredentialsException(BadCredentialsException e){
        RestMenssagemErro restMenssagemErro = new RestMenssagemErro(HttpStatus.UNAUTHORIZED,
                e.getMessage(),
                Set.of());
        log.warn("event=authentication_failed message=invalid_credentials");
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(restMenssagemErro);
    }

    @ExceptionHandler(InvalidBearerTokenException.class)
    public ResponseEntity<RestMenssagemErro> handleAuthenticationException(InvalidBearerTokenException e){
        RestMenssagemErro restMenssagemErro = new RestMenssagemErro(HttpStatus.UNAUTHORIZED,
                "Sessão inválida ou expirada",
                Set.of());
        log.warn("event=authentication_token_failure status=401 reason=invalid_bearer_token");
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(restMenssagemErro);
    }

    @ExceptionHandler(InsufficientAuthenticationException.class)
    public ResponseEntity<RestMenssagemErro> handleInsufficientAuthenticationException(InsufficientAuthenticationException e){
        RestMenssagemErro restMenssagemErro = new RestMenssagemErro(HttpStatus.UNAUTHORIZED,
                "É necessária autenticação completa para acessar este recurso.",
                Set.of());
        log.warn("event=authentication_missing status=401 message=insufficient_authentication");
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(restMenssagemErro);
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<RestMenssagemErro> handleAccessDeniedException(AccessDeniedException e){
        RestMenssagemErro restMenssagemErro = new RestMenssagemErro(HttpStatus.FORBIDDEN,
                "Você não tem permissão para acessar este recurso.",
                Set.of());
        log.warn("event=access_denied status=403 message=forbidden");
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(restMenssagemErro);
    }

    @ExceptionHandler(RefreshTokenExpiradoException.class)
    public ResponseEntity<RestMenssagemErro> handleRefreshTokenExpiradoException(RefreshTokenExpiradoException e){
        RestMenssagemErro restMenssagemErro = new RestMenssagemErro(HttpStatus.UNAUTHORIZED,
                e.getMessage(),
                Set.of());
        log.warn("event=refresh_token_expired status=401 message=token_expired");
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(restMenssagemErro);
    }

    @ExceptionHandler(RefreshTokenInvalidoException.class)
    public ResponseEntity<RestMenssagemErro> handleRefreshTokenInvalidoException(RefreshTokenInvalidoException e){
        RestMenssagemErro restMenssagemErro = new RestMenssagemErro(HttpStatus.UNAUTHORIZED,
                e.getMessage(),
                Set.of());
        log.warn("event=refresh_token_invalid status=401 message=invalid_token");
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(restMenssagemErro);
    }

    @ExceptionHandler(RefreshTokenRevogadoException.class)
    public ResponseEntity<RestMenssagemErro> handleRefreshTokenRevogadoException(RefreshTokenRevogadoException e){
        RestMenssagemErro restMenssagemErro = new RestMenssagemErro(HttpStatus.UNAUTHORIZED,
                e.getMessage(),
                Set.of());
        log.warn("event=refresh_token_revoked status=401 message=token_revoked");
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(restMenssagemErro);
    }

    @ExceptionHandler(SessaoExpiradaException.class)
    public ResponseEntity<RestMenssagemErro> handleSessaoExpiradaException(SessaoExpiradaException e){
        RestMenssagemErro restMenssagemErro = new RestMenssagemErro(HttpStatus.UNAUTHORIZED,
                e.getMessage(),
                Set.of());
        log.warn("event=session_expired status=401 message=session_expired");
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(restMenssagemErro);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<RestMenssagemErro> handleErrosNaoTratados(Exception e){
        RestMenssagemErro restMenssagemErro = new RestMenssagemErro(HttpStatus.INTERNAL_SERVER_ERROR,
                "Ocorreu um erro inesperado.",
                Set.of());
        log.error("event=unexpected_error status=500 exception={} message=internal_server_error", e.getClass().getSimpleName(), e);
        return ResponseEntity.internalServerError().body(restMenssagemErro);
    }
}
