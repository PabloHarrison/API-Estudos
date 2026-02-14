package com.example.DBEstudosAPI.configuration;

import com.example.DBEstudosAPI.security.CustomAuthenticationEntryPoint;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfiguration {

    private final CustomAuthenticationEntryPoint entryPoint;

    public SecurityConfiguration(CustomAuthenticationEntryPoint entryPoint) {
        this.entryPoint = entryPoint;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception{
        http
                .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/auth/register/**").permitAll()
                        .requestMatchers("/auth/login/**").permitAll()
                        .anyRequest().authenticated())
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .oauth2ResourceServer(oauth2ResourceServer -> oauth2ResourceServer
                        .jwt(Customizer.withDefaults())
                        .authenticationEntryPoint(entryPoint));
        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder(10);
    }

    @Bean
    public WebSecurityCustomizer webSecurityCustomizer(){
        return web -> web
                .ignoring()
                .requestMatchers(
                        "/v2/api-docs/**",
                        "/v3/api-docs/**",
                        "/swagger-ui.html",
                        "/swagger-resources/**",
                        "/swagger-ui/**",
                        "/webjars/**");
    }
}
