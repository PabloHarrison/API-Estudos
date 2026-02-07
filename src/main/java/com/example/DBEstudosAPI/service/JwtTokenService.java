package com.example.DBEstudosAPI.service;

import com.example.DBEstudosAPI.entities.Usuario;
import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.security.oauth2.resource.OAuth2ResourceServerProperties;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class JwtTokenService {

    @Value("${api.security.token.secret}")
    private String SECRET_KEY;

    public String generateToken(Usuario usuario){
        Map<String, Object> claimsUsuario = new HashMap<>();
        claimsUsuario.put("id", usuario.getId());
        claimsUsuario.put("role", usuario.getRoles());
        return Jwts
                .builder()
                .claims(claimsUsuario)
                .subject(usuario.getEmail())
                .issuedAt(Date.from(Instant.now()))
                .expiration(Date.from(Instant.now().plusSeconds(3600)))
                .signWith(secretKey())
                .compact();
    }

    public SecretKey secretKey(){
        byte[] keyBytes = SECRET_KEY.getBytes(StandardCharsets.UTF_8);
        return Keys.hmacShaKeyFor(keyBytes);
    }
}
