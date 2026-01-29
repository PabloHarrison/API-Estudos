package com.example.DBEstudosAPI.controller.mappers;

import com.example.DBEstudosAPI.controller.dto.CategoriaPostDTO;
import com.example.DBEstudosAPI.controller.dto.CategoriaResponseDTO;
import com.example.DBEstudosAPI.entities.Categoria;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface CategoriaMapper {

    Categoria toEntity(CategoriaPostDTO dto);

    CategoriaResponseDTO toDTO(Categoria categoria);
}
