package com.example.DBEstudosAPI.dto;

import jakarta.validation.constraints.NotBlank;

public record TokenResponseDTO(
        @NotBlank(message = "Campo obrigatório!") String accessToken,
        @NotBlank(message = "Campo obrigatório!") String refreshToken) {
}
