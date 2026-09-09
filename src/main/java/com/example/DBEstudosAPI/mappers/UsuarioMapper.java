package com.example.DBEstudosAPI.mappers;

import com.example.DBEstudosAPI.dto.UsuarioPostDTO;
import com.example.DBEstudosAPI.dto.UsuarioResponseDTO;
import com.example.DBEstudosAPI.entities.Usuario;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface UsuarioMapper {

    Usuario toEntity(UsuarioPostDTO usuarioPostDTO);

    UsuarioResponseDTO toDTO(Usuario usuario);
}
