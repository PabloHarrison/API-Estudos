package com.example.DBEstudosAPI.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

@Schema(name = "TokenResponse")
public record TokenResponseDTO(
        @NotBlank(message = "Campo obrigatório!") String accessToken,
        @NotBlank(message = "Campo obrigatório!") String refreshToken) {
}
