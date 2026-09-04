package com.example.DBEstudosAPI.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Schema(name = "UsuarioRequest")
public record UsuarioPostDTO(
        @NotBlank(message = "Campo obrigatório!") String login,
        @NotBlank(message = "Campo obrigatório!") @Email(message = "Inválido") String email,
        @NotBlank(message = "Campo obrigatório!") @Size(min = 12, max = 100) String password){
}
