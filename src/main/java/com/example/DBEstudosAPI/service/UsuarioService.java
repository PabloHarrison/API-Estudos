package com.example.DBEstudosAPI.service;

import com.example.DBEstudosAPI.configuration.JwtProperties;
import com.example.DBEstudosAPI.dto.TokenResponseDTO;
import com.example.DBEstudosAPI.dto.UsuarioLoginDTO;
import com.example.DBEstudosAPI.dto.UsuarioPostDTO;
import com.example.DBEstudosAPI.exceptions.UsuarioNaoEncontradoException;
import com.example.DBEstudosAPI.mappers.UsuarioMapper;
import com.example.DBEstudosAPI.entities.Usuario;
import com.example.DBEstudosAPI.enums.Roles;
import com.example.DBEstudosAPI.repository.UsuarioRepository;
import com.example.DBEstudosAPI.validator.UsuarioValidator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Service
@RequiredArgsConstructor
@Slf4j
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final UsuarioMapper usuarioMapper;
    private final PasswordEncoder encoder;
    private final UsuarioValidator validator;
    private final JwtTokenService jwtTokenService;
    private final RefreshTokenService refreshTokenService;
    private final JwtProperties jwtProperties;

    public Usuario findByLogin(String login) {
        return usuarioRepository.findByLogin(login).orElseThrow(() -> new UsuarioNaoEncontradoException("Usuário não encontrado!"));
    }

    public Usuario findByEmail(String email) {
        return usuarioRepository.findByEmail(email).orElseThrow(() -> new UsuarioNaoEncontradoException("Usuário não encontrado!"));
    }

    public void registerUser(UsuarioPostDTO dto) {
        validator.usuarioValidator(dto.login(), dto.email());
        Usuario usuario;
        usuario = usuarioMapper.toEntity(dto);
        usuario.setPassword(encoder.encode(dto.password()));
        usuario.setRoles(Roles.USER);
        Usuario usuarioSalvo = usuarioRepository.save(usuario);
        log.info("event=user_registered usuarioId={}", usuarioSalvo.getId());
    }

    public TokenResponseDTO loginUser(UsuarioLoginDTO dto) {
        Usuario usuario = findByEmail(dto.email());
        boolean senha = encoder.matches(dto.password(), usuario.getPassword());
        if (!senha) {
            authenticationFailed(dto.email());
        }
        log.info("event=usuario_authenticated usuarioId={}", usuario.getId());
        String accessToken = jwtTokenService.generateToken(usuario);
        String refreshToken = refreshTokenService.buildAndSaveRefreshToken(usuario.getId(), Instant.now().plus(jwtProperties.getSessionDuration()));
        return new TokenResponseDTO(accessToken, refreshToken);
    }

    private void authenticationFailed(String email) {
        log.warn("event=authentication_failed");
        throw new BadCredentialsException("Credencial errada!");
    }
}
