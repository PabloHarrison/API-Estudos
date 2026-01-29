package com.example.DBEstudosAPI.controller.dto;

import java.time.LocalDate;
import java.util.Set;
import java.util.UUID;

public record RegistroPatchDTO(
        LocalDate data,
        Integer horasEstudadas,
        String anotacao,
        String resumo,
        String planejamento,
        Set<UUID> categoriasIds){
}