package com.example.DBEstudosAPI.dto;

public record RegisterResponseDTO(
        UsuarioResponseDTO usuario,
        TokenResponseDTO tokens) {
}
