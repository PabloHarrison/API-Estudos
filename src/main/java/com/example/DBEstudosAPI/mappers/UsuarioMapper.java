package com.example.DBEstudosAPI.mappers;

import com.example.DBEstudosAPI.dto.UsuarioPostDTO;
import com.example.DBEstudosAPI.entities.Usuario;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface UsuarioMapper {

    public Usuario toEntity(UsuarioPostDTO usuarioPostDTO);
}
