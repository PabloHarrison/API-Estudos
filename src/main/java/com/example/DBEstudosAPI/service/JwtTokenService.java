package com.example.DBEstudosAPI.service;

import com.example.DBEstudosAPI.entities.Usuario;
import lombok.RequiredArgsConstructor;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.security.oauth2.server.authorization.settings.TokenSettings;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Service
@RequiredArgsConstructor
public class JwtTokenService {

    private final TokenSettings tokenSettings;
    private final JwtEncoder jwtEncoder;

    public String generateToken(Usuario usuario){
        Instant now = Instant.now();
        JwtClaimsSet jwtClaimsSet = JwtClaimsSet
                .builder()
                .claim("email", usuario.getEmail())
                .claim("scope", usuario.getRoles())
                .subject(usuario.getId().toString())
                .issuedAt(now)
                .expiresAt(now.plus(tokenSettings.getAccessTokenTimeToLive()))
                .build();
        return jwtEncoder.encode(JwtEncoderParameters.from(jwtClaimsSet)).getTokenValue();
    }
}
