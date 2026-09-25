package com.example.DBEstudosAPI.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Schema(name = "CategoriaRequest")
public record CategoriaPostDTO(
        @NotBlank(message = "Nome da categoria é obrigatório.") @Size(max = 30) String nomeCategoria,
        @NotBlank(message = "Cor é obrigatório") String cor){
}
