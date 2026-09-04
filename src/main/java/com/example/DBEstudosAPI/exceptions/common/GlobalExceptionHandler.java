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
    public ResponseEntity<RestMensagemErro> handleRegistroNaoEncontrado(RegistroNaoEncontradoException e){
        RestMensagemErro restMensagemErro = new RestMensagemErro(HttpStatus.NOT_FOUND, e.getMessage(), Set.of());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(restMensagemErro);
    }

    @ExceptionHandler(CategoriaNaoEncontradaException.class)
    public ResponseEntity<RestMensagemErro> handleCategoriaNaoEncontradaException(CategoriaNaoEncontradaException e){
        RestMensagemErro restMensagemErro = new RestMensagemErro(HttpStatus.NOT_FOUND, e.getMessage(), Set.of());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(restMensagemErro);
    }

    @ExceptionHandler(UsuarioNaoEncontradoException.class)
    public ResponseEntity<RestMensagemErro> handleUsuarioNaoEncontradoException(UsuarioNaoEncontradoException e){
        RestMensagemErro restMensagemErro = new RestMensagemErro(
                HttpStatus.NOT_FOUND,
                e.getMessage(),
                Set.of());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(restMensagemErro);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<RestMensagemErro> handleMethodArgumentNotValidException(MethodArgumentNotValidException e){
        Set<RestCampoErro> erros = e.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(fe -> new RestCampoErro(fe.getField(), fe.getDefaultMessage()))
                .collect(Collectors.toSet());

        RestMensagemErro restMensagemErro = new RestMensagemErro(
                HttpStatus.BAD_REQUEST, "Erro de validação", erros);
        log.warn("event=validation_failed status=400 error_count={} fields={} message=invalid_request_data",
                erros.size(),
                erros);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(restMensagemErro);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<RestMensagemErro> handleHttpMessageNotReadableException(HttpMessageNotReadableException e){
        RestMensagemErro restMensagemErro = new RestMensagemErro(HttpStatus.BAD_REQUEST,
                "JSON inválido ou campo com formato incorreto",
                Set.of());
        log.warn("event=request_malformed status=400 error={} message=invalid_request_body", e.getMessage());
        return ResponseEntity.badRequest().body(restMensagemErro);
    }

    @ExceptionHandler(CategoriaNaoPermitidaException.class)
    public ResponseEntity<RestMensagemErro> handleCategoriaNaoPermitidaException(CategoriaNaoPermitidaException e){
        RestMensagemErro restMensagemErro = new RestMensagemErro(HttpStatus.BAD_REQUEST,
                e.getMessage(),
                Set.of());
        log.warn("event=business_rule_violation message=category_not_allowed detail={}", e.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(restMensagemErro);
    }

    @ExceptionHandler(CategoriaEmUsoException.class)
    public ResponseEntity<RestMensagemErro> handleCategoriaEmUsoException(CategoriaEmUsoException e){
        RestMensagemErro restMensagemErro = new RestMensagemErro(HttpStatus.CONFLICT,
                e.getMessage(),
                Set.of());
        log.warn("event=category_in_use status=409 message=resource_in_use detail={}", e.getMessage());
        return ResponseEntity.status(HttpStatus.CONFLICT).body(restMensagemErro);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<RestMensagemErro> handleIllegalArgumentException(IllegalArgumentException e){
        RestMensagemErro restMensagemErro = new RestMensagemErro(HttpStatus.BAD_REQUEST,
                e.getMessage(),
                Set.of());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(restMensagemErro);
    }

    @ExceptionHandler(LoginCadastradoException.class)
    public ResponseEntity<RestMensagemErro> handleLoginCadastradoException(LoginCadastradoException e){
        RestMensagemErro restMensagemErro = new RestMensagemErro(HttpStatus.CONFLICT,
                e.getMessage(),
                Set.of());
        log.warn("event=duplicate_registration_by_login status=409 detail={}", e.getMessage());
        return ResponseEntity.status(HttpStatus.CONFLICT).body(restMensagemErro);
    }

    @ExceptionHandler(EmailCadastradoException.class)
    public ResponseEntity<RestMensagemErro> handleEmailCadastradoException(EmailCadastradoException e){
        RestMensagemErro restMensagemErro = new RestMensagemErro(HttpStatus.CONFLICT,
                e.getMessage(),
                Set.of());
        log.warn("event=duplicate_registration_by_email status=409 detail={}", e.getMessage());
        return ResponseEntity.status(HttpStatus.CONFLICT).body(restMensagemErro);
    }

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<RestMensagemErro> handleBadCredentialsException(BadCredentialsException e){
        RestMensagemErro restMensagemErro = new RestMensagemErro(HttpStatus.UNAUTHORIZED,
                e.getMessage(),
                Set.of());
        log.warn("event=authentication_failed message=invalid_credentials");
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(restMensagemErro);
    }

    @ExceptionHandler(InvalidBearerTokenException.class)
    public ResponseEntity<RestMensagemErro> handleAuthenticationException(InvalidBearerTokenException e){
        RestMensagemErro restMensagemErro = new RestMensagemErro(HttpStatus.UNAUTHORIZED,
                "Sessão inválida ou expirada",
                Set.of());
        log.warn("event=authentication_token_failure status=401 reason=invalid_bearer_token");
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(restMensagemErro);
    }

    @ExceptionHandler(InsufficientAuthenticationException.class)
    public ResponseEntity<RestMensagemErro> handleInsufficientAuthenticationException(InsufficientAuthenticationException e){
        RestMensagemErro restMensagemErro = new RestMensagemErro(HttpStatus.UNAUTHORIZED,
                "É necessária autenticação completa para acessar este recurso.",
                Set.of());
        log.warn("event=authentication_missing status=401 message=insufficient_authentication");
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(restMensagemErro);
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<RestMensagemErro> handleAccessDeniedException(AccessDeniedException e){
        RestMensagemErro restMensagemErro = new RestMensagemErro(HttpStatus.FORBIDDEN,
                "Você não tem permissão para acessar este recurso.",
                Set.of());
        log.warn("event=access_denied status=403 message=forbidden");
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(restMensagemErro);
    }

    @ExceptionHandler(RefreshTokenExpiradoException.class)
    public ResponseEntity<RestMensagemErro> handleRefreshTokenExpiradoException(RefreshTokenExpiradoException e){
        RestMensagemErro restMensagemErro = new RestMensagemErro(HttpStatus.UNAUTHORIZED,
                e.getMessage(),
                Set.of());
        log.warn("event=refresh_token_expired status=401 message=token_expired");
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(restMensagemErro);
    }

    @ExceptionHandler(RefreshTokenInvalidoException.class)
    public ResponseEntity<RestMensagemErro> handleRefreshTokenInvalidoException(RefreshTokenInvalidoException e){
        RestMensagemErro restMensagemErro = new RestMensagemErro(HttpStatus.UNAUTHORIZED,
                e.getMessage(),
                Set.of());
        log.warn("event=refresh_token_invalid status=401 message=invalid_token");
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(restMensagemErro);
    }

    @ExceptionHandler(RefreshTokenRevogadoException.class)
    public ResponseEntity<RestMensagemErro> handleRefreshTokenRevogadoException(RefreshTokenRevogadoException e){
        RestMensagemErro restMensagemErro = new RestMensagemErro(HttpStatus.UNAUTHORIZED,
                e.getMessage(),
                Set.of());
        log.warn("event=refresh_token_revoked status=401 message=token_revoked");
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(restMensagemErro);
    }

    @ExceptionHandler(SessaoExpiradaException.class)
    public ResponseEntity<RestMensagemErro> handleSessaoExpiradaException(SessaoExpiradaException e){
        RestMensagemErro restMensagemErro = new RestMensagemErro(HttpStatus.UNAUTHORIZED,
                e.getMessage(),
                Set.of());
        log.warn("event=session_expired status=401 message=session_expired");
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(restMensagemErro);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<RestMensagemErro> handleErrosNaoTratados(Exception e){
        RestMensagemErro restMensagemErro = new RestMensagemErro(HttpStatus.INTERNAL_SERVER_ERROR,
                "Ocorreu um erro inesperado.",
                Set.of());
        log.error("event=unexpected_error status=500 exception={} message=internal_server_error", e.getClass().getSimpleName(), e);
        return ResponseEntity.internalServerError().body(restMensagemErro);
    }
}
