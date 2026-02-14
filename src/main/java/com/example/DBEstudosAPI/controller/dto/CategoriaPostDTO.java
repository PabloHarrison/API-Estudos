package com.example.DBEstudosAPI.controller.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

@Schema(name = "CategoriaRequest")
public record CategoriaPostDTO(
        @NotBlank(message = "Nome da categoria é obrigatório.") String nomeCategoria){
}
