package com.example.DBEstudosAPI.controller.dto;

import jakarta.validation.constraints.*;

import java.time.LocalDate;
import java.util.Set;
import java.util.UUID;

public record RegistroPostDTO(
        @NotNull(message = "Data é obrigatoria") LocalDate data,
        @NotNull(message = "Tempo é obrigatorio") @Positive(message = "Obrigatorio ser positivo") Integer horasEstudadas,
        @Size(max = 1000) String anotacao,
        @Size(max = 300) String resumo,
        @Size(max = 300) String planejamento,
        @NotEmpty(message = "Categoria é obrigatoria") Set<UUID> categoriasIds) {
}
