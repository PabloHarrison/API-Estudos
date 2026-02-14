package com.example.DBEstudosAPI.controller.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

@Schema(name = "LoginRequest")
public record UsuarioLoginDTO(
        @NotBlank(message = "Campo obrigatório!") @Email String email,
        @NotBlank(message = "Campo obrigatório!") String password){
}
