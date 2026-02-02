package com.example.DBEstudosAPI.service;

import com.example.DBEstudosAPI.controller.dto.UsuarioPostDTO;
import com.example.DBEstudosAPI.controller.mappers.UsuarioMapper;
import com.example.DBEstudosAPI.entities.Usuario;
import com.example.DBEstudosAPI.repository.UsuarioRepository;
import com.example.DBEstudosAPI.validator.UsuarioValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final UsuarioMapper usuarioMapper;
    private final PasswordEncoder encoder;
    private final UsuarioValidator validator;

    public Usuario findByLogin(String login) {
        return usuarioRepository.findByLogin(login);
    }

    public void saveUser(UsuarioPostDTO dto) {
        validator.usuarioValidator(dto.login(), dto.email());
        Usuario usuario;
        usuario = usuarioMapper.toEntity(dto);
        String senha = usuario.getPassword();
        usuario.setPassword(encoder.encode(senha));
        usuarioRepository.save(usuario);
    }
}
