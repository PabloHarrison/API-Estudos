package com.example.DBEstudosAPI.validator;

import com.example.DBEstudosAPI.entities.Usuario;
import com.example.DBEstudosAPI.exceptions.EmailCadastradoException;
import com.example.DBEstudosAPI.exceptions.LoginCadastradoException;
import com.example.DBEstudosAPI.repository.UsuarioRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

@ExtendWith(MockitoExtension.class)
public class UsuarioValidatorTest {

    @InjectMocks
    UsuarioValidator validator;

    @Mock
    UsuarioRepository usuarioRepository;

    private Usuario criarUsuario(){
        Usuario u = new Usuario();
        u.setId(UUID.randomUUID());
        u.setLogin("Pablo");
        u.setEmail("email@gmail.com");
        return u;
    }

    @Test
    void deveValidarUsuario(){
        Usuario u = criarUsuario();

        Mockito.when(usuarioRepository.findByLogin(Mockito.any())).thenReturn(Optional.empty());
        Mockito.when(usuarioRepository.findByEmail(Mockito.any())).thenReturn(Optional.empty());

        Assertions.assertThatCode(() -> validator.usuarioValidator(u.getLogin(), u.getEmail())).doesNotThrowAnyException();

        Mockito.verify(usuarioRepository).findByLogin(u.getLogin());
        Mockito.verify(usuarioRepository).findByEmail(u.getEmail());
    }

    @Test
    void deveLancarExcecaoQuandoLoginEncontrado(){
        Usuario u = criarUsuario();

        Mockito.when(usuarioRepository.findByLogin(Mockito.any())).thenReturn(Optional.of(u));

        Throwable erro = Assertions.catchThrowable(() -> validator.usuarioValidator(u.getLogin(), u.getEmail()));

        Assertions.assertThat(erro).isInstanceOf(LoginCadastradoException.class).hasMessage("Login cadastrado.");

        Mockito.verify(usuarioRepository, Mockito.times(1)).findByLogin(u.getLogin());
    }

    @Test
    void deveLancarExcecaoQuandoEmailEncontrado(){
        Usuario u = criarUsuario();

        Mockito.when(usuarioRepository.findByEmail(Mockito.any())).thenReturn(Optional.of(u));

        Throwable erro = Assertions.catchThrowable(() -> validator.usuarioValidator(u.getLogin(), u.getEmail()));

        Assertions.assertThat(erro).isInstanceOf(EmailCadastradoException.class).hasMessage("Email cadastrado.");

        Mockito.verify(usuarioRepository, Mockito.times(1)).findByEmail(u.getEmail());
    }
}
