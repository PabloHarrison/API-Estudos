package com.example.DBEstudosAPI.controller.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record UsuarioLoginDTO(
        @NotBlank(message = "Campo obrigatório!") @Email String email,
        @NotBlank(message = "Campo obrigatório!") String password){
}
