package com.example.DBEstudosAPI.controller.dto;

import java.util.UUID;

public record CategoriaResponseDTO(
        UUID id,
        String nomeCategoria) {
}
