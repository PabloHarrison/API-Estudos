package com.example.DBEstudosAPI.controller.dto;

import com.example.DBEstudosAPI.enuns.Roles;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record UsuarioPostDTO(
        @NotBlank(message = "Campo obrigatório!") String login,
        @NotBlank(message = "Campo obrigatório!") @Email(message = "Inválido") String email,
        @NotBlank(message = "Campo obrigatório!") String password,
        @NotNull Roles role) {
}
