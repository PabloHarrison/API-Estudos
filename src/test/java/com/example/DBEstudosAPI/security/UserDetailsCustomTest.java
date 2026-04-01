package com.example.DBEstudosAPI.security;

import com.example.DBEstudosAPI.entities.Usuario;
import com.example.DBEstudosAPI.enums.Roles;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;

public class UserDetailsCustomTest {

    @Test
    void deveRetornarAuthoritiesAdmin(){
        Usuario u = new Usuario();
        u.setRoles(Roles.ADMIN);

        UserDetailsCustom userDetailsCustom = new UserDetailsCustom(u);

        Collection<? extends GrantedAuthority> authorities = userDetailsCustom.getAuthorities();

        Assertions.assertThat(authorities).extracting(GrantedAuthority::getAuthority).containsExactlyInAnyOrder("ROLE_ADMIN", "ROLE_USER");
    }

    @Test
    void deveRetornarAuthoritiesUser(){
        Usuario u = new Usuario();
        u.setRoles(Roles.USER);

        UserDetailsCustom userDetailsCustom = new UserDetailsCustom(u);

        Collection<? extends GrantedAuthority> authorities = userDetailsCustom.getAuthorities();

        Assertions.assertThat(authorities).extracting(GrantedAuthority::getAuthority).contains("ROLE_USER");
    }

    @Test
    void deveRetornarPassword(){
        Usuario u = new Usuario();
        u.setPassword("123");

        UserDetailsCustom userDetailsCustom = new UserDetailsCustom(u);

        Assertions.assertThat(userDetailsCustom.getPassword()).isEqualTo("123");
    }

    @Test
    void deveRetornarUsername(){
        Usuario u = new Usuario();
        u.setLogin("pablo");

        UserDetailsCustom userDetailsCustom = new UserDetailsCustom(u);

        Assertions.assertThat(userDetailsCustom.getUsername()).isEqualTo("pablo");
    }

    @Test
    void deveRetornarVerdadeiroParaMetodosBoolean(){
        Usuario u = new Usuario();

        UserDetailsCustom userDetailsCustom = new UserDetailsCustom(u);

        Assertions.assertThat(userDetailsCustom.isAccountNonExpired()).isTrue();
        Assertions.assertThat(userDetailsCustom.isAccountNonLocked()).isTrue();
        Assertions.assertThat(userDetailsCustom.isCredentialsNonExpired()).isTrue();
        Assertions.assertThat(userDetailsCustom.isEnabled()).isTrue();
    }
}
