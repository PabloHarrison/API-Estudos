package com.example.DBEstudosAPI.service;

import com.example.DBEstudosAPI.controller.dto.UsuarioLoginDTO;
import com.example.DBEstudosAPI.controller.dto.UsuarioPostDTO;
import com.example.DBEstudosAPI.controller.mappers.UsuarioMapper;
import com.example.DBEstudosAPI.entities.Usuario;
import com.example.DBEstudosAPI.enuns.Roles;
import com.example.DBEstudosAPI.repository.UsuarioRepository;
import com.example.DBEstudosAPI.validator.UsuarioValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final UsuarioMapper usuarioMapper;
    private final PasswordEncoder encoder;
    private final UsuarioValidator validator;
    private final JwtTokenService jwtTokenService;

    public Usuario findByLogin(String login) {
        return usuarioRepository.findByLogin(login).orElseThrow();
    }

    public Usuario findByEmail(String email){
        return usuarioRepository.findByEmail(email).orElseThrow(()-> new BadCredentialsException("Credencial errada!"));
    }

    public void registerUser(UsuarioPostDTO dto) {
        validator.usuarioValidator(dto.login(), dto.email());
        Usuario usuario;
        usuario = usuarioMapper.toEntity(dto);
        String senha = usuario.getPassword();
        usuario.setPassword(encoder.encode(senha));
        usuario.setRoles(Roles.USER);
        usuarioRepository.save(usuario);
    }

    public String loginUser(UsuarioLoginDTO dto){
        Usuario usuario = findByEmail(dto.email());
        boolean senha = encoder.matches(dto.password(), usuario.getPassword());
        if(senha){
            return jwtTokenService.generateToken(usuario);
        }
        throw new BadCredentialsException("Credencial errada!");
    }
}
