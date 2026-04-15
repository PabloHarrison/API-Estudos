package com.example.DBEstudosAPI.service;

import com.example.DBEstudosAPI.entities.Usuario;
import com.example.DBEstudosAPI.security.UserDetailsCustom;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthorizationService implements UserDetailsService {

    private final UsuarioService usuarioService;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Usuario usuario = usuarioService.findByLogin(username);
        if (usuario == null) {
            log.warn("event=user_not_found username={} message=authentication_failed", username);
            throw new UsernameNotFoundException("Usuário não encontrado.");
        }
        return new UserDetailsCustom(usuario);

    }
}
