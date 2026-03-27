package com.example.DBEstudosAPI.service;

import com.example.DBEstudosAPI.entities.Usuario;
import com.example.DBEstudosAPI.enums.Roles;
import com.example.DBEstudosAPI.security.UserDetailsCustom;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.UUID;

@ExtendWith(MockitoExtension.class)
public class AuthorizationServiceTest {

    @InjectMocks
    AuthorizationService authorizationService;

    @Mock
    UsuarioService usuarioService;

    private Usuario criarUsuario() {
        Usuario u = new Usuario();
        u.setId(UUID.fromString("7dce5f33-8375-4514-85c8-9968681c4815"));
        u.setLogin("pablo");
        u.setEmail("email@gmail.com");
        u.setPassword("123");
        u.setRoles(Roles.ADMIN);
        return u;
    }

    @Test
    void deveAcharUmUsuarioERetornarUmUserDetailsCustom(){
        String username = "pablo";
        Usuario u = criarUsuario();

        Mockito.when(usuarioService.findByLogin(Mockito.anyString())).thenReturn(u);

        UserDetails userDetails = authorizationService.loadUserByUsername(username);

        Assertions.assertThat(userDetails).isInstanceOf(UserDetailsCustom.class);
        Assertions.assertThat(userDetails.getUsername()).isEqualTo(u.getLogin());
        Assertions.assertThat(userDetails.getPassword()).isEqualTo(u.getPassword());
        Assertions.assertThat(userDetails.getAuthorities()).extracting(GrantedAuthority::getAuthority).contains("ROLE_ADMIN", "ROLE_USER");
    }

    @Test
    void deveLancarExcecaoSeUsuarioForNull(){
        String username = "pablo";

        Mockito.when(usuarioService.findByLogin(Mockito.anyString())).thenReturn(null);

        Throwable erro = Assertions.catchThrowable(() -> authorizationService.loadUserByUsername(username));

        Assertions.assertThat(erro).isInstanceOf(UsernameNotFoundException.class).hasMessage("Usuário não encontrado.");
    }
}
