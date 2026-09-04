package com.example.DBEstudosAPI.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;

import java.time.LocalDate;
import java.util.Set;
import java.util.UUID;

@Schema(name = "RegistroRequest")
public record RegistroPostDTO(
        @NotNull(message = "Data é obrigatoria") LocalDate data,
        @NotNull(message = "Tempo é obrigatorio") @Positive(message = "Obrigatorio ser positivo") Integer tempoEmMinutos,
        @Size(max = 2000) String resumo,
        @Size(max = 2000) String planejamento,
        @NotEmpty(message = "Categoria é obrigatoria") Set<UUID> categoriasIds) {
}
