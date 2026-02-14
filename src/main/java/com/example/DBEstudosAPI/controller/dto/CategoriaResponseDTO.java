package com.example.DBEstudosAPI.controller.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.UUID;

@Schema(name = "CategoriaResponse")
public record CategoriaResponseDTO(
        UUID id,
        String nomeCategoria) {
}
