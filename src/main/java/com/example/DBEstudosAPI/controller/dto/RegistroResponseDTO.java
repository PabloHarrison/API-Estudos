package com.example.DBEstudosAPI.controller.dto;

import java.time.LocalDate;
import java.util.Set;
import java.util.UUID;

public record RegistroResponseDTO(
        UUID id,
        LocalDate data,
        Integer horasEstudadas,
        String anotacao,
        String resumo,
        String planejamento,
        Set<CategoriaResponseDTO> categorias) {
}