package com.example.DBEstudosAPI.controller.mappers;

import com.example.DBEstudosAPI.controller.dto.RegistroPostDTO;
import com.example.DBEstudosAPI.controller.dto.RegistroResponseDTO;
import com.example.DBEstudosAPI.entities.Registro;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface RegistroMapper {

    Registro toEntity(RegistroPostDTO postDTO);

    RegistroResponseDTO toDTO(Registro registro);
}
