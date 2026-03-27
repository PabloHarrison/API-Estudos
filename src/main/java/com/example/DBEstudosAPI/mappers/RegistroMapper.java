package com.example.DBEstudosAPI.mappers;

import com.example.DBEstudosAPI.dto.RegistroPostDTO;
import com.example.DBEstudosAPI.dto.RegistroResponseDTO;
import com.example.DBEstudosAPI.entities.Registro;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface RegistroMapper {

    Registro toEntity(RegistroPostDTO postDTO);

    RegistroResponseDTO toDTO(Registro registro);
}
