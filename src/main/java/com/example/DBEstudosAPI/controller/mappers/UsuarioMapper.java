package com.example.DBEstudosAPI.controller.mappers;

import com.example.DBEstudosAPI.controller.dto.UsuarioPostDTO;
import com.example.DBEstudosAPI.entities.Usuario;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface UsuarioMapper {

    public Usuario toEntity(UsuarioPostDTO usuarioPostDTO);
}
