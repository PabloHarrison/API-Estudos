package com.example.DBEstudosAPI.dto;

import jakarta.validation.constraints.NotBlank;

public record RefreshTokenRequestDTO(
        @NotBlank(message = "Campo obrigatório!") String refreshToken) {
}
