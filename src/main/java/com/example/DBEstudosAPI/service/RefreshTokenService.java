package com.example.DBEstudosAPI.service;

import com.example.DBEstudosAPI.dto.RefreshTokenRequestDTO;
import com.example.DBEstudosAPI.dto.TokenResponseDTO;
import com.example.DBEstudosAPI.entities.RefreshToken;
import com.example.DBEstudosAPI.entities.Usuario;
import com.example.DBEstudosAPI.exceptions.*;
import com.example.DBEstudosAPI.repository.RefreshTokenRepository;
import com.example.DBEstudosAPI.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.time.Duration;
import java.time.Instant;
import java.util.Base64;

@Slf4j
@Service
@RequiredArgsConstructor
public class RefreshTokenService {

    private final RefreshTokenRepository refreshTokenRepository;
    private final JwtTokenService tokenService;
    private final UsuarioRepository usuarioRepository;

    public String generateRefreshToken() {
        SecureRandom sr = new SecureRandom();
        byte[] seed = new byte[32];
        sr.nextBytes(seed);
        return Base64.getUrlEncoder().encodeToString(seed);
    }

    public String createSession(Usuario usuario) {
        String refreshTokenEncoded = generateRefreshToken();
        RefreshToken refreshTokenEntity = new RefreshToken();
        refreshTokenEntity.setTokenHash(refreshTokenEncoded);
        refreshTokenEntity.setUserId(usuario.getId());
        refreshTokenEntity.setExpiresAt(Instant.now().plus(Duration.ofDays(7)));
        refreshTokenEntity.setSessaoExpiresAt(Instant.now().plus(Duration.ofDays(14)));
        refreshTokenEntity.setRevogado(false);
        refreshTokenRepository.save(refreshTokenEntity);
        return refreshTokenEncoded;
    }

    public String refreshSession(RefreshToken refreshToken) {
        String refreshTokenEncoded = generateRefreshToken();
        RefreshToken refreshTokenEntity = new RefreshToken();
        refreshTokenEntity.setTokenHash(refreshTokenEncoded);
        refreshTokenEntity.setUserId(refreshToken.getUserId());
        refreshTokenEntity.setExpiresAt(Instant.now().plus(Duration.ofDays(7)));
        refreshTokenEntity.setSessaoExpiresAt(refreshToken.getSessaoExpiresAt());
        refreshTokenEntity.setRevogado(false);
        refreshTokenRepository.save(refreshTokenEntity);
        return refreshTokenEncoded;
    }

    @Transactional
    public TokenResponseDTO refresh(RefreshTokenRequestDTO dto) {
        RefreshToken refreshTokenEncontrado = refreshTokenRepository.findByTokenHash(dto.refreshToken()).orElseThrow(() -> new RefreshTokenInvalidoException("Refresh Token inválido!"));
        if (refreshTokenEncontrado.isRevogado()) {
            throw new RefreshTokenRevogadoException("Refresh Token Revogado!");
        }
        if (refreshTokenEncontrado.getExpiresAt().isBefore(Instant.now())) {
            throw new RefreshTokenExpiradoException("Refresh Token Expirado!");
        }
        if (refreshTokenEncontrado.getSessaoExpiresAt().isBefore(Instant.now())) {
            throw new SessaoExpiradaException("Sessão de Refresh Token Expirada!");
        }
        Usuario usuario = usuarioRepository.findById(refreshTokenEncontrado.getUserId()).orElseThrow(() -> {
            UsuarioNaoEncontradoException e = new UsuarioNaoEncontradoException("Usuário não Encontrado!");
            log.error(
                    "event=user_not_found_during_token_refresh user_id={} message=unexpected_state",
                    refreshTokenEncontrado.getUserId(), e);
            return e;
        });

        refreshTokenEncontrado.setRevogado(true);
        String accessToken = tokenService.generateToken(usuario);
        String refreshToken = refreshSession(refreshTokenEncontrado);
        refreshTokenRepository.save(refreshTokenEncontrado);
        return new TokenResponseDTO(accessToken, refreshToken);
    }
}
