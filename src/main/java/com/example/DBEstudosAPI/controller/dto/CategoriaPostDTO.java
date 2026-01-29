package com.example.DBEstudosAPI.controller.dto;

import jakarta.validation.constraints.NotBlank;

public record CategoriaPostDTO(
        @NotBlank(message = "Nome da categoria é obrigatório.") String nomeCategoria){
}
