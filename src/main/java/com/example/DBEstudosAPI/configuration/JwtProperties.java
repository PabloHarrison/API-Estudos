package com.example.DBEstudosAPI.configuration;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.time.Duration;

@ConfigurationProperties(prefix = "spring.jwt")
@Getter
@Setter
public class JwtProperties {

    private String privateKeyPath;
    private String publicKeyPath;
    private Duration refreshTokenDuration;
    private Duration sessionDuration;
}
