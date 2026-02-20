package com.example.DBEstudosAPI.service;

import com.example.DBEstudosAPI.controller.dto.UsuarioLoginDTO;
import com.example.DBEstudosAPI.controller.dto.UsuarioPostDTO;
import com.example.DBEstudosAPI.controller.mappers.UsuarioMapper;
import com.example.DBEstudosAPI.entities.Usuario;
import com.example.DBEstudosAPI.enuns.Roles;
import com.example.DBEstudosAPI.repository.UsuarioRepository;
import com.example.DBEstudosAPI.validator.UsuarioValidator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final UsuarioMapper usuarioMapper;
    private final PasswordEncoder encoder;
    private final UsuarioValidator validator;
    private final JwtTokenService jwtTokenService;

    public Usuario findByLogin(String login) {
        return usuarioRepository.findByLogin(login).orElseThrow();
    }

    public Optional<Usuario> findByEmail(String email) {
        return usuarioRepository.findByEmail(email);
    }

    public void registerUser(UsuarioPostDTO dto) {
        validator.usuarioValidator(dto.login(), dto.email());
        Usuario usuario;
        usuario = usuarioMapper.toEntity(dto);
        usuario.setPassword(encoder.encode(dto.password()));
        usuario.setRoles(Roles.USER);
        Usuario usuarioSalvo = usuarioRepository.save(usuario);
        log.info("event=registered_usuario usuarioId={} login={}", usuarioSalvo.getId(), usuarioSalvo.getLogin());
    }

    public String loginUser(UsuarioLoginDTO dto) {
        Optional<Usuario> optionalUsuario = findByEmail(dto.email());
        if(optionalUsuario.isEmpty()){
            authenticationFailed(dto.email());
            throw new BadCredentialsException("Credencial errada!");
        }
        Usuario usuario = optionalUsuario.get();
        boolean senha = encoder.matches(dto.password(), usuario.getPassword());
        if (!senha) {
            authenticationFailed(dto.email());
            throw new BadCredentialsException("Credencial errada!");
        }
        log.info("event=usuario_authenticated usuarioId={}", usuario.getId());
        return jwtTokenService.generateToken(usuario);
    }

    private void authenticationFailed(String email) {
        log.warn("event=authentication_failed email={}", email);
        throw new BadCredentialsException("Credencial errada!");
    }
}
