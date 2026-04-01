package com.example.DBEstudosAPI.repository;

import com.example.DBEstudosAPI.entities.Usuario;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.Optional;

@DataJpaTest
@ActiveProfiles("test")
public class UsuarioRepositoryTest {

    @Autowired
    UsuarioRepository repository;

    @Test
    void deveBuscarUsuarioPeloLogin(){
        Usuario u = new Usuario();
        u.setLogin("pablo");

        repository.save(u);
        Optional<Usuario> usuario = repository.findByLogin(u.getLogin());

        Assertions.assertThat(usuario).isPresent().get().extracting(Usuario::getLogin).isEqualTo("pablo");
        Assertions.assertThat(repository.findAll()).hasSize(1);
    }

    @Test
    void deveBuscarUsuarioPeloLoginRetornandoVazio(){
        Usuario u = new Usuario();
        u.setLogin("pablo");

        Optional<Usuario> usuario = repository.findByLogin(u.getLogin());

        Assertions.assertThat(usuario).isEmpty();
    }

    @Test
    void deveBuscarUsuarioPeloEmail(){
        Usuario u = new Usuario();
        u.setEmail("email@gmail.com");

        repository.save(u);
        Optional<Usuario> usuario = repository.findByEmail(u.getEmail());

        Assertions.assertThat(usuario).isPresent().get().extracting(Usuario::getEmail).isEqualTo("email@gmail.com");
        Assertions.assertThat(repository.findAll()).hasSize(1);
    }

    @Test
    void deveBuscarUsuarioPeloEmailRetornandoVazio(){
        Usuario u = new Usuario();
        u.setEmail("email@gmail.com");

        Optional<Usuario> usuario = repository.findByEmail(u.getEmail());

        Assertions.assertThat(usuario).isEmpty();
    }
}
