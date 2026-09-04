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
        @Positive Integer horasEstudadas,
        @Size(max = 1000) String anotacao,
        @Size(max = 300) String resumo,
        @Size(max = 300) String planejamento,
        Set<UUID> categoriasIds) {
}