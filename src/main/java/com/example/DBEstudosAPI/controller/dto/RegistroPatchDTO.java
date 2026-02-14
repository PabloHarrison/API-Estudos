package com.example.DBEstudosAPI.controller.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDate;
import java.util.Set;
import java.util.UUID;

@Schema(name = "UpdateRegistroRequest")
public record RegistroPatchDTO(
        LocalDate data,
        Integer horasEstudadas,
        String anotacao,
        String resumo,
        String planejamento,
        Set<UUID> categoriasIds){
}