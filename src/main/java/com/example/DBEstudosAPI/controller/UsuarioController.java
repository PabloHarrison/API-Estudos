package com.example.DBEstudosAPI.controller;

import com.example.DBEstudosAPI.controller.dto.UsuarioLoginDTO;
import com.example.DBEstudosAPI.controller.dto.UsuarioPostDTO;
import com.example.DBEstudosAPI.entities.Usuario;
import com.example.DBEstudosAPI.service.UsuarioService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("auth")
@RequiredArgsConstructor
public class UsuarioController {

    private final UsuarioService usuarioService;

    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    public void registrar(@RequestBody @Valid UsuarioPostDTO dto){
        usuarioService.registerUser(dto);
    }

    @PostMapping("/login")
    @ResponseStatus(HttpStatus.OK)
    public String logar(@RequestBody @Valid UsuarioLoginDTO dto) {
        return usuarioService.loginUser(dto);
    }
}
