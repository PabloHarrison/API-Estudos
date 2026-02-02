package com.example.DBEstudosAPI.controller.mappers;

import com.example.DBEstudosAPI.controller.dto.UsuarioPostDTO;
import com.example.DBEstudosAPI.entities.Usuario;
import com.example.DBEstudosAPI.enuns.Roles;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public class UsuarioMapper {

    public Usuario toEntity(UsuarioPostDTO usuarioPostDTO){
        if(usuarioPostDTO == null){
            return null;
        }
        Usuario usuario = new Usuario();
        usuario.setLogin(usuarioPostDTO.login());
        usuario.setEmail(usuarioPostDTO.email());
        usuario.setPassword(usuarioPostDTO.password());
        usuario.setRoles(Roles.value(usuarioPostDTO.role().getRole()));
        return usuario;
    }
}
