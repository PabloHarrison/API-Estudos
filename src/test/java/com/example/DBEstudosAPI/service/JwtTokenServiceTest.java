package com.example.DBEstudosAPI.service;

import com.example.DBEstudosAPI.entities.Usuario;
import com.example.DBEstudosAPI.enums.Roles;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.security.oauth2.server.authorization.settings.TokenSettings;

import java.time.Instant;
import java.util.UUID;

@ExtendWith(MockitoExtension.class)
public class JwtTokenServiceTest {

    @InjectMocks
    JwtTokenService service;

    @Mock
    TokenSettings settings;
    @Mock
    JwtEncoder jwtEncoder;
    @Mock
    Jwt jwt;

    @Test
    void deveGerarToken() {
        Usuario u = new Usuario();
        u.setId(UUID.randomUUID());
        u.setEmail("email@gmail.com");
        u.setRoles(Roles.ADMIN);

        Mockito.when(jwt.getTokenValue()).thenReturn("token");
        Mockito.when(jwtEncoder.encode(Mockito.any())).thenReturn(jwt);

        String token = service.generateToken(u);

        Assertions.assertThat(token).isEqualTo("token");

        ArgumentCaptor<JwtEncoderParameters> captor = ArgumentCaptor.forClass(JwtEncoderParameters.class);
        Mockito.verify(jwtEncoder).encode(captor.capture());
    }
}
