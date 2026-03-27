package com.example.DBEstudosAPI.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(name = "UpdateCategoriaRequest")
public record CategoriaPatchDTO (
        String nomeCategoria){
}
