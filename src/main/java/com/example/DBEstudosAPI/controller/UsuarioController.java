package com.example.DBEstudosAPI.controller;

import com.example.DBEstudosAPI.controller.dto.UsuarioLoginDTO;
import com.example.DBEstudosAPI.controller.dto.UsuarioPostDTO;
import com.example.DBEstudosAPI.entities.Usuario;
import com.example.DBEstudosAPI.service.UsuarioService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("auth")
@RequiredArgsConstructor
@Tag(name = "Usuario")
public class UsuarioController {

    private final UsuarioService usuarioService;

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
    public String logar(@RequestBody @Valid UsuarioLoginDTO dto) {
        return usuarioService.loginUser(dto);
    }
}
