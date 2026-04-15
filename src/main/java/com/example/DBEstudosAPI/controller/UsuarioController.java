package com.example.DBEstudosAPI.controller;

import com.example.DBEstudosAPI.dto.RefreshTokenRequestDTO;
import com.example.DBEstudosAPI.dto.TokenResponseDTO;
import com.example.DBEstudosAPI.dto.UsuarioLoginDTO;
import com.example.DBEstudosAPI.dto.UsuarioPostDTO;
import com.example.DBEstudosAPI.service.RefreshTokenService;
import com.example.DBEstudosAPI.service.UsuarioService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("auth")
@RequiredArgsConstructor
@Tag(name = "Usuario")
public class UsuarioController {

    private final UsuarioService usuarioService;
    private final RefreshTokenService refreshTokenService;

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
    public void registrar(@RequestBody @Valid UsuarioPostDTO dto){
        usuarioService.registerUser(dto);
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
    public TokenResponseDTO logar(@RequestBody @Valid UsuarioLoginDTO dto) {
        return usuarioService.loginUser(dto);
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
    public TokenResponseDTO refresh(@RequestBody @Valid RefreshTokenRequestDTO dto){
        return refreshTokenService.refresh(dto);
    }
}
