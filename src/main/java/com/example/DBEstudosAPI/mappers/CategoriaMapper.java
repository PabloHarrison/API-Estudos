package com.example.DBEstudosAPI.mappers;

import com.example.DBEstudosAPI.dto.CategoriaPostDTO;
import com.example.DBEstudosAPI.dto.CategoriaResponseDTO;
import com.example.DBEstudosAPI.entities.Categoria;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface CategoriaMapper {

    Categoria toEntity(CategoriaPostDTO dto);

    CategoriaResponseDTO toDTO(Categoria categoria);
}
