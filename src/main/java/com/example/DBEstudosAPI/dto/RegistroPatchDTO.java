package com.example.DBEstudosAPI.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;
import java.util.Set;
import java.util.UUID;

@Schema(name = "UpdateRegistroRequest")
public record RegistroPatchDTO(
        @PastOrPresent LocalDate data,
        @Positive Integer tempoEmMinutos,
        @Size(max = 2000) String resumo,
        @Size(max = 2000) String planejamento,
        Set<UUID> categoriasIds) {
}