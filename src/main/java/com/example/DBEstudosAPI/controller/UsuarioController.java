package com.example.DBEstudosAPI.controller;

import com.example.DBEstudosAPI.configuration.JwtProperties;
import com.example.DBEstudosAPI.dto.*;
import com.example.DBEstudosAPI.service.RefreshTokenService;
import com.example.DBEstudosAPI.service.UsuarioService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;

@RestController
@RequestMapping("auth")
@RequiredArgsConstructor
@Tag(name = "Usuario")
public class UsuarioController {

    private final UsuarioService usuarioService;
    private final RefreshTokenService refreshTokenService;
    private final JwtProperties jwtProperties;

    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(
            summary = "Registrar usuário",
            description = "Registra um novo usuário a partir dos dados informados na requisição.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Usuário registrado com sucesso."),
            @ApiResponse(responseCode = "400", description = "Erro de validação ou JSON inválido."),
            @ApiResponse(responseCode = "409", description = "Usuário já cadastrado")
    })
    public ResponseEntity<RegisterResponseDTO> registrar(@RequestBody @Valid UsuarioPostDTO dto) {
        RegisterResponseDTO responseDTO = usuarioService.registerUser(dto);

        ResponseCookie accessToken = ResponseCookie.from("accessToken", responseDTO.tokens().accessToken())
                .httpOnly(true)
                .secure(false)
                .path("/")
                .maxAge(jwtProperties.getAccessTokenDuration())
                .sameSite("Strict")
                .build();

        ResponseCookie refreshToken = ResponseCookie.from("refreshToken", responseDTO.tokens().refreshToken())
                .httpOnly(true)
                .secure(false)
                .path("/")
                .maxAge(jwtProperties.getRefreshTokenDuration())
                .sameSite("Strict")
                .build();

        return ResponseEntity.created(URI.create("/usuarios/" + responseDTO.usuario().id()))
                .headers(httpHeaders -> {
                    httpHeaders.add(HttpHeaders.SET_COOKIE, accessToken.toString());
                    httpHeaders.add(HttpHeaders.SET_COOKIE, refreshToken.toString());
                })
                .body(responseDTO);
    }

    @PostMapping("/login")
    @ResponseStatus(HttpStatus.OK)
    @Operation(
            summary = "Logar usuário",
            description = "Loga um usuário já cadastrado a partir dos dados informados na requisição.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Usuário logado com sucesso."),
            @ApiResponse(responseCode = "400", description = "Erro de validação ou JSON inválido."),
            @ApiResponse(responseCode = "401", description = "Credentiais inválidas.")
    })
    public ResponseEntity<TokenResponseDTO> logar(@RequestBody @Valid UsuarioLoginDTO dto) {
        TokenResponseDTO responseDTO = usuarioService.loginUser(dto);

        ResponseCookie accessToken = ResponseCookie.from("accessToken", responseDTO.accessToken())
                .httpOnly(true)
                .secure(false)
                .path("/")
                .maxAge(jwtProperties.getAccessTokenDuration())
                .sameSite("Strict")
                .build();

        ResponseCookie refreshToken = ResponseCookie.from("refreshToken", responseDTO.refreshToken())
                .httpOnly(true)
                .secure(false)
                .path("/")
                .maxAge(jwtProperties.getRefreshTokenDuration())
                .sameSite("Strict")
                .build();

        return ResponseEntity.status(HttpStatus.OK)
                .headers(httpHeaders -> {
                    httpHeaders.add(HttpHeaders.SET_COOKIE, accessToken.toString());
                    httpHeaders.add(HttpHeaders.SET_COOKIE, refreshToken.toString());
                })
                .build();
    }

    @PostMapping("/refresh")
    @ResponseStatus(HttpStatus.OK)
    @Operation(
            summary = "Renovar access token e refresh token",
            description = "Renovar os tokens do usuário a partir do refresh token anterior.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Tokens renovadas com sucesso."),
            @ApiResponse(responseCode = "400", description = "Erro de validação ou JSON inválido."),
            @ApiResponse(responseCode = "401", description = "Refresh token inválido, expirado ou revogado.")
    })
    public ResponseEntity<TokenResponseDTO> refresh(@CookieValue("refreshToken") String refreshTokenCookie) {
        TokenResponseDTO responseDTO = refreshTokenService.refresh(refreshTokenCookie);

        ResponseCookie accessToken = ResponseCookie.from("accessToken", responseDTO.accessToken())
                .httpOnly(true)
                .secure(false)
                .path("/")
                .maxAge(jwtProperties.getAccessTokenDuration())
                .sameSite("Strict")
                .build();

        ResponseCookie refreshToken = ResponseCookie.from("refreshToken", responseDTO.refreshToken())
                .httpOnly(true)
                .secure(false)
                .path("/")
                .maxAge(jwtProperties.getRefreshTokenDuration())
                .sameSite("Strict")
                .build();

        return ResponseEntity.status(HttpStatus.OK)
                .headers(httpHeaders -> {
                    httpHeaders.add(HttpHeaders.SET_COOKIE, accessToken.toString());
                    httpHeaders.add(HttpHeaders.SET_COOKIE, refreshToken.toString());
                })
                .build();
    }
}
