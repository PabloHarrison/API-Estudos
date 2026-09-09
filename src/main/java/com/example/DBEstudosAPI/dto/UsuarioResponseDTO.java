package com.example.DBEstudosAPI.dto;

import com.example.DBEstudosAPI.enums.Roles;

import java.util.UUID;

public record UsuarioResponseDTO(
        UUID id,
        String login,
        String email,
        String password,
        Roles roles) {
}
