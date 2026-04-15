package com.example.DBEstudosAPI.service;

import com.example.DBEstudosAPI.dto.RefreshTokenRequestDTO;
import com.example.DBEstudosAPI.dto.TokenResponseDTO;
import com.example.DBEstudosAPI.entities.RefreshToken;
import com.example.DBEstudosAPI.entities.Usuario;
import com.example.DBEstudosAPI.exceptions.*;
import com.example.DBEstudosAPI.repository.RefreshTokenRepository;
import com.example.DBEstudosAPI.repository.UsuarioRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@ExtendWith(MockitoExtension.class)
public class RefreshTokenServiceTest {

    @InjectMocks
    RefreshTokenService refreshTokenService;
    @Mock
    RefreshTokenRepository refreshTokenRepository;
    @Mock
    JwtTokenService jwtTokenService;
    @Mock
    UsuarioRepository usuarioRepository;

    private Usuario criarUsuario() {
        Usuario u = new Usuario();
        u.setId(UUID.fromString("7dce5f33-8375-4514-85c8-9968681c4815"));
        return u;
    }

    private RefreshToken criarRefreshToken(Usuario u) {
        RefreshToken refreshTokenEntity = new RefreshToken();
        String refreshTokenEncoded = refreshTokenService.generateRefreshToken();
        refreshTokenEntity.setTokenHash(refreshTokenEncoded);
        refreshTokenEntity.setUserId(u.getId());
        refreshTokenEntity.setExpiresAt(Instant.now().plus(Duration.ofDays(7)));
        refreshTokenEntity.setSessaoExpiresAt(Instant.now().plus(Duration.ofDays(14)));
        refreshTokenEntity.setRevogado(false);
        return refreshTokenEntity;
    }

    @Test
    void deveGerarRefreshTokenNaoNull() {
        String refreshToken = refreshTokenService.generateRefreshToken();

        Assertions.assertThat(refreshToken)
                .isNotNull()
                .isNotEmpty();
    }

    @Test
    void deveGerarRefreshTokenDoTamanhoCorreto() {
        String refreshToken = refreshTokenService.generateRefreshToken();

        Assertions.assertThat(refreshToken)
                .hasSizeBetween(43, 44);
    }

    @Test
    void deveGerarRefreshTokenComFormatoValido() {
        String refreshToken = refreshTokenService.generateRefreshToken();

        Assertions.assertThat(refreshToken)
                .matches("^[A-Za-z0-9_-]+={0,2}$");
    }

    @Test
    void deveGerarRefreshTokensDiferentes() {
        String refreshToken1 = refreshTokenService.generateRefreshToken();
        String refreshToken2 = refreshTokenService.generateRefreshToken();

        Assertions.assertThat(refreshToken1)
                .isNotEqualTo(refreshToken2);
    }

    @Test
    void deveCriarSessao() {
        Usuario u = criarUsuario();
        RefreshToken refreshTokenEntity = criarRefreshToken(u);

        Mockito.when(refreshTokenRepository.save(Mockito.any())).thenAnswer(invocationOnMock -> invocationOnMock.getArgument(0));

        String refreshToken = refreshTokenService.createSession(u);

        ArgumentCaptor<RefreshToken> captor = ArgumentCaptor.forClass(RefreshToken.class);

        Mockito.verify(refreshTokenRepository).save(captor.capture());
        RefreshToken saved = captor.getValue();

        Assertions.assertThat(refreshToken).isNotNull()
                .isNotEmpty()
                .isEqualTo(saved.getTokenHash());
        Assertions.assertThat(saved.getUserId()).isEqualTo(u.getId());
        Assertions.assertThat(saved.isRevogado()).isFalse();
        Assertions.assertThat(saved.getExpiresAt()).isAfter(Instant.now());
        Assertions.assertThat(saved.getSessaoExpiresAt()).isAfter(saved.getExpiresAt());
    }

    @Test
    void deveRenovarSessao() {
        Usuario u = criarUsuario();
        RefreshToken refreshToken = criarRefreshToken(u);

        Mockito.when(refreshTokenRepository.save(Mockito.any())).thenAnswer(invocationOnMock -> invocationOnMock.getArgument(0));

        String refreshTokenSession = refreshTokenService.refreshSession(refreshToken);

        ArgumentCaptor<RefreshToken> captor = ArgumentCaptor.forClass(RefreshToken.class);

        Mockito.verify(refreshTokenRepository).save(captor.capture());
        RefreshToken saved = captor.getValue();

        Assertions.assertThat(refreshTokenSession).isNotNull()
                .isNotEmpty()
                .isEqualTo(saved.getTokenHash());
        Assertions.assertThat(saved.getTokenHash()).isNotEqualTo(refreshToken.getTokenHash());
        Assertions.assertThat(saved.getUserId()).isEqualTo(refreshToken.getUserId());
        Assertions.assertThat(saved.isRevogado()).isFalse();
        Assertions.assertThat(saved.getExpiresAt()).isAfter(Instant.now());
        Assertions.assertThat(saved.getSessaoExpiresAt()).isEqualTo(refreshToken.getSessaoExpiresAt());
    }

    @Test
    void deveCriarFluxoDeRefreshToken() {
        Usuario u = criarUsuario();
        RefreshToken refreshTokenEncontrado = criarRefreshToken(u);
        RefreshTokenRequestDTO requestDTO = new RefreshTokenRequestDTO(refreshTokenEncontrado.getTokenHash());

        Mockito.when(refreshTokenRepository.findByTokenHash(Mockito.anyString())).thenReturn(Optional.of(refreshTokenEncontrado));
        Mockito.when(usuarioRepository.findById(Mockito.any())).thenReturn(Optional.of(u));
        Mockito.when(jwtTokenService.generateToken(Mockito.any())).thenReturn("accessToken");
        Mockito.when(refreshTokenRepository.save(Mockito.any())).thenAnswer(invocationOnMock -> invocationOnMock.getArgument(0));

        TokenResponseDTO responseDTO = refreshTokenService.refresh(requestDTO);

        ArgumentCaptor<RefreshToken> captor = ArgumentCaptor.forClass(RefreshToken.class);

        Mockito.verify(refreshTokenRepository, Mockito.times(2)).save(captor.capture());
        List<RefreshToken> savedList = captor.getAllValues();

        RefreshToken antigo = savedList.stream()
                .filter(RefreshToken::isRevogado)
                .findFirst()
                .orElseThrow();
        RefreshToken novo = savedList.stream()
                .filter(t -> !t.isRevogado())
                .findFirst()
                .orElseThrow();

        Assertions.assertThat(antigo.getTokenHash())
                .isEqualTo(refreshTokenEncontrado.getTokenHash());
        Assertions.assertThat(antigo.isRevogado())
                .isTrue();
        Assertions.assertThat(antigo.getUserId())
                .isEqualTo(u.getId());
        Assertions.assertThat(novo.getTokenHash())
                .isNotNull()
                .isNotEmpty()
                .isNotEqualTo(refreshTokenEncontrado.getTokenHash());
        Assertions.assertThat(novo.isRevogado())
                .isFalse();
        Assertions.assertThat(novo.getUserId())
                .isEqualTo(u.getId());
        Assertions.assertThat(novo.getSessaoExpiresAt())
                .isEqualTo(refreshTokenEncontrado.getSessaoExpiresAt());
        Assertions.assertThat(novo.getExpiresAt())
                .isAfter(Instant.now());
        Assertions.assertThat(responseDTO.accessToken())
                .isNotNull()
                .isNotEmpty();
        Assertions.assertThat(responseDTO.refreshToken())
                .isEqualTo(novo.getTokenHash());
    }

    @Test
    void deveLancarExcecaoRefreshTokenInvalidoNoFluxo(){
        Usuario u = criarUsuario();
        RefreshToken refreshTokenEncontrado = criarRefreshToken(u);
        RefreshTokenRequestDTO requestDTO = new RefreshTokenRequestDTO(refreshTokenEncontrado.getTokenHash());

        Mockito.when(refreshTokenRepository.findByTokenHash(Mockito.anyString())).thenReturn(Optional.empty());

        Throwable erro = Assertions.catchThrowable(() -> refreshTokenService.refresh(requestDTO));

        Assertions.assertThat(erro).isInstanceOf(RefreshTokenInvalidoException.class).hasMessage("Refresh Token inválido!");

        Mockito.verify(refreshTokenRepository, Mockito.times(1)).findByTokenHash(Mockito.any());
    }

    @Test
    void deveLancarExcecaoRefreshTokenRevogadoNoFluxo(){
        Usuario u = criarUsuario();
        RefreshToken refreshTokenEncontrado = criarRefreshToken(u);
        refreshTokenEncontrado.setRevogado(true);
        RefreshTokenRequestDTO requestDTO = new RefreshTokenRequestDTO(refreshTokenEncontrado.getTokenHash());

        Mockito.when(refreshTokenRepository.findByTokenHash(Mockito.anyString())).thenReturn(Optional.of(refreshTokenEncontrado));

        Throwable erro = Assertions.catchThrowable(() -> refreshTokenService.refresh(requestDTO));

        Assertions.assertThat(erro).isInstanceOf(RefreshTokenRevogadoException.class).hasMessage("Refresh Token Revogado!");

        Mockito.verify(refreshTokenRepository, Mockito.times(1)).findByTokenHash(Mockito.any());
    }

    @Test
    void deveLancarExcecaoRefreshTokenExpiradoNoFluxo(){
        Usuario u = criarUsuario();
        RefreshToken refreshTokenEncontrado = criarRefreshToken(u);
        Instant past = Instant.now().minus(Duration.ofMinutes(5));
        refreshTokenEncontrado.setExpiresAt(past);
        RefreshTokenRequestDTO requestDTO = new RefreshTokenRequestDTO(refreshTokenEncontrado.getTokenHash());

        Mockito.when(refreshTokenRepository.findByTokenHash(Mockito.anyString())).thenReturn(Optional.of(refreshTokenEncontrado));

        Throwable erro = Assertions.catchThrowable(() -> refreshTokenService.refresh(requestDTO));

        Assertions.assertThat(erro).isInstanceOf(RefreshTokenExpiradoException.class).hasMessage("Refresh Token Expirado!");

        Mockito.verify(refreshTokenRepository, Mockito.times(1)).findByTokenHash(Mockito.any());
    }

    @Test
    void deveLancarExcecaoSessaoExpiradaNoFluxo(){
        Usuario u = criarUsuario();
        RefreshToken refreshTokenEncontrado = criarRefreshToken(u);
        Instant past = Instant.now().minus(Duration.ofMinutes(5));
        refreshTokenEncontrado.setSessaoExpiresAt(past);
        RefreshTokenRequestDTO requestDTO = new RefreshTokenRequestDTO(refreshTokenEncontrado.getTokenHash());

        Mockito.when(refreshTokenRepository.findByTokenHash(Mockito.anyString())).thenReturn(Optional.of(refreshTokenEncontrado));

        Throwable erro = Assertions.catchThrowable(() -> refreshTokenService.refresh(requestDTO));

        Assertions.assertThat(erro).isInstanceOf(SessaoExpiradaException.class).hasMessage("Sessão de Refresh Token Expirada!");

        Mockito.verify(refreshTokenRepository, Mockito.times(1)).findByTokenHash(Mockito.any());
    }

    @Test
    void deveLancarExcecaoUsuarioNaoEncontradoNoFluxo(){
        Usuario u = criarUsuario();
        RefreshToken refreshTokenEncontrado = criarRefreshToken(u);
        RefreshTokenRequestDTO requestDTO = new RefreshTokenRequestDTO(refreshTokenEncontrado.getTokenHash());

        Mockito.when(refreshTokenRepository.findByTokenHash(Mockito.anyString())).thenReturn(Optional.of(refreshTokenEncontrado));
        Mockito.when(usuarioRepository.findById(Mockito.any())).thenReturn(Optional.empty());

        Throwable erro = Assertions.catchThrowable(() -> refreshTokenService.refresh(requestDTO));

        Assertions.assertThat(erro).isInstanceOf(UsuarioNaoEncontradoException.class).hasMessage("Usuário não Encontrado!");

        Mockito.verify(refreshTokenRepository, Mockito.times(1)).findByTokenHash(Mockito.any());
        Mockito.verify(usuarioRepository, Mockito.times(1)).findById(Mockito.any());
    }

}
