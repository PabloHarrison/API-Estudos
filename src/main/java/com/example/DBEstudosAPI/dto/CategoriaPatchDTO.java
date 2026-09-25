package com.example.DBEstudosAPI.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

@Schema(name = "UpdateCategoriaRequest")
public record CategoriaPatchDTO (
        @Size(max = 30) String nomeCategoria,
        @Pattern(regexp = "^#[0-9A-Fa-f]{6}$") String cor){
}