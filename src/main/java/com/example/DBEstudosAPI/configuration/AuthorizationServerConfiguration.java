package com.example.DBEstudosAPI.configuration;


import com.example.DBEstudosAPI.exceptions.RSAKeyException;
import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.RSAKey;
import com.nimbusds.jose.jwk.source.ImmutableJWKSet;
import com.nimbusds.jose.jwk.source.JWKSource;
import com.nimbusds.jose.proc.SecurityContext;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.NimbusJwtEncoder;
import org.springframework.security.oauth2.server.authorization.config.annotation.web.configuration.OAuth2AuthorizationServerConfiguration;
import org.springframework.security.oauth2.server.authorization.settings.OAuth2TokenFormat;
import org.springframework.security.oauth2.server.authorization.settings.TokenSettings;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.security.GeneralSecurityException;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.time.Duration;
import java.util.Base64;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class  AuthorizationServerConfiguration {

    private final JwtProperties jwtProperties;

    private RSAKey carregarRSAKey(){
        try {
            String untreatedPrivateKey = Files.readString(Path.of(jwtProperties.getPrivateKeyPath()));
            String untreatedPublicKey = Files.readString(Path.of(jwtProperties.getPublicKeyPath()));

            String cleanedPrivateKey = untreatedPrivateKey
                    .replace("-----BEGIN PRIVATE KEY-----", "")
                    .replace("-----END PRIVATE KEY-----", "")
                    .replaceAll("\\s", "");

            String cleanedPublicKey = untreatedPublicKey
                    .replace("-----BEGIN PUBLIC KEY-----", "")
                    .replace("-----END PUBLIC KEY-----", "")
                    .replaceAll("\\s", "");

            byte[] privateKeyByte = Base64.getDecoder().decode(cleanedPrivateKey);
            byte[] publicKeyByte = Base64.getDecoder().decode(cleanedPublicKey);

            PKCS8EncodedKeySpec privateKey = new PKCS8EncodedKeySpec(privateKeyByte);
            X509EncodedKeySpec publicKey = new X509EncodedKeySpec(publicKeyByte);

            KeyFactory keyFactory = KeyFactory.getInstance("RSA");
            RSAPrivateKey rsaPrivateKey = (RSAPrivateKey) keyFactory.generatePrivate(privateKey);
            RSAPublicKey rsaPublicKey = (RSAPublicKey) keyFactory.generatePublic(publicKey);

            return new RSAKey
                    .Builder(rsaPublicKey)
                    .privateKey(rsaPrivateKey)
                    .keyID("main-key")
                    .build();
        }
        catch (NoSuchFileException e){
            throw new RSAKeyException("Arquivo da chave RSA não encontrado:" + e.getFile(), e);
        }
        catch (IOException e){
            throw new RSAKeyException("Não foi possível ler os arquivos das chaves RSA.", e);
        }
        catch (GeneralSecurityException e){
            throw new RSAKeyException("Não foi possível gerar as chaves RSA.", e);
        }
        catch (IllegalArgumentException e){
            throw new RSAKeyException("As chaves RSA possuem formato inválido.", e);
        }
    }

    @Bean
    public JWKSource<SecurityContext> jwkSource() throws Exception {
        RSAKey rsaKey = carregarRSAKey();
        JWKSet jwkSet = new JWKSet(rsaKey);
        return new ImmutableJWKSet<>(jwkSet);
    }

    @Bean
    public JwtDecoder jwtDecoder(JWKSource<SecurityContext> jwkSource){
        return OAuth2AuthorizationServerConfiguration.jwtDecoder(jwkSource);
    }

    @Bean
    public JwtEncoder jwtEncoder(JWKSource<SecurityContext> jwkSource){
        return new NimbusJwtEncoder(jwkSource);
    }

    @Bean
    public TokenSettings tokenSettings(){
        return TokenSettings
                .builder()
                .accessTokenFormat(OAuth2TokenFormat.SELF_CONTAINED)
                .accessTokenTimeToLive(Duration.ofMinutes(15))
                .refreshTokenTimeToLive(Duration.ofDays(7))
                .build();
    }
}
