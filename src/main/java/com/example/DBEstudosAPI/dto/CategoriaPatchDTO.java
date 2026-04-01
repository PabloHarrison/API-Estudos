package com.example.DBEstudosAPI.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

@Schema(name = "UpdateCategoriaRequest")
public record CategoriaPatchDTO (
        @NotBlank String nomeCategoria){
}
