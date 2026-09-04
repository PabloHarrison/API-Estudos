package com.example.DBEstudosAPI.service;

import com.example.DBEstudosAPI.configuration.JwtProperties;
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

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.time.Instant;
import java.util.Base64;
import java.util.HexFormat;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class RefreshTokenService {

    private final RefreshTokenRepository refreshTokenRepository;
    private final JwtTokenService tokenService;
    private final UsuarioRepository usuarioRepository;
    private final JwtProperties jwtProperties;

    public String generateRefreshToken() {
        SecureRandom sr = new SecureRandom();
        byte[] seed = new byte[32];
        sr.nextBytes(seed);
        return Base64.getUrlEncoder().encodeToString(seed);
    }

    public String encodeRefreshTokenToHash(String refreshToken) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(refreshToken.getBytes(StandardCharsets.UTF_8));
            return HexFormat.of().formatHex(hash);
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException("O SHA-256 não está disponível.", e);
        }
    }

    public String buildAndSaveRefreshToken(UUID usuarioId, Instant sessaoExpiresAt) {
        String refreshTokenEncoded = generateRefreshToken();
        String refreshTokenHash = encodeRefreshTokenToHash(refreshTokenEncoded);
        RefreshToken refreshTokenEntity = new RefreshToken();
        refreshTokenEntity.setTokenHash(refreshTokenHash);
        refreshTokenEntity.setUsuario_id(usuarioId);
        refreshTokenEntity.setExpiresAt(Instant.now().plus(jwtProperties.getRefreshTokenDuration()));
        refreshTokenEntity.setSessaoExpiresAt(sessaoExpiresAt);
        refreshTokenEntity.setRevogado(false);
        refreshTokenRepository.save(refreshTokenEntity);
        return refreshTokenEncoded;
    }

    @Transactional
    public TokenResponseDTO refresh(RefreshTokenRequestDTO dto) {
        RefreshToken refreshTokenEncontrado = refreshTokenRepository.findByTokenHash(encodeRefreshTokenToHash(dto.refreshToken())).orElseThrow(() -> new RefreshTokenInvalidoException("Refresh Token inválido!"));
        if (refreshTokenEncontrado.isRevogado()) {
            throw new RefreshTokenRevogadoException("Refresh Token Revogado!");
        }
        if (refreshTokenEncontrado.getExpiresAt().isBefore(Instant.now())) {
            throw new RefreshTokenExpiradoException("Refresh Token Expirado!");
        }
        if (refreshTokenEncontrado.getSessaoExpiresAt().isBefore(Instant.now())) {
            throw new SessaoExpiradaException("Sessão de Refresh Token Expirada!");
        }
        Usuario usuario = usuarioRepository.findById(refreshTokenEncontrado.getUsuario_id()).orElseThrow(() -> {
            UsuarioNaoEncontradoException e = new UsuarioNaoEncontradoException("Usuário não Encontrado!");
            log.error(
                    "event=user_not_found_during_token_refresh user_id={} message=unexpected_state",
                    refreshTokenEncontrado.getUsuario_id(), e);
            return e;
        });

        refreshTokenEncontrado.setRevogado(true);
        String accessToken = tokenService.generateToken(usuario);
        String refreshToken = buildAndSaveRefreshToken(refreshTokenEncontrado.getUsuario_id(), refreshTokenEncontrado.getSessaoExpiresAt());
        refreshTokenRepository.save(refreshTokenEncontrado);
        return new TokenResponseDTO(accessToken, refreshToken);
    }
}
