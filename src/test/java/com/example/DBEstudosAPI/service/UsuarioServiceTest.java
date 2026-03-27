package com.example.DBEstudosAPI.service;

import com.example.DBEstudosAPI.dto.UsuarioLoginDTO;
import com.example.DBEstudosAPI.dto.UsuarioPostDTO;
import com.example.DBEstudosAPI.entities.Usuario;
import com.example.DBEstudosAPI.enums.Roles;
import com.example.DBEstudosAPI.exceptions.UsuarioNaoEncontradoException;
import com.example.DBEstudosAPI.mappers.UsuarioMapper;
import com.example.DBEstudosAPI.repository.UsuarioRepository;
import com.example.DBEstudosAPI.validator.UsuarioValidator;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;
import java.util.UUID;

@ExtendWith(MockitoExtension.class)
public class UsuarioServiceTest {

    @InjectMocks
    UsuarioService service;

    @Mock
    UsuarioRepository usuarioRepository;
    @Mock
    UsuarioMapper mapper;
    @Mock
    PasswordEncoder encoder;
    @Mock
    UsuarioValidator validator;
    @Mock
    JwtTokenService jwtTokenService;

    private Usuario criarUsuario(){
        Usuario u = new Usuario();
        u.setId(UUID.fromString("7dce5f33-8375-4514-85c8-9968681c4815"));
        u.setLogin("Pablo");
        u.setEmail("email@gmail.com");
        u.setPassword("123");
        u.setRoles(Roles.ADMIN);
        return u;
    }

    @Test
    void deveAcharUsuarioPeloLogin(){
        Usuario u = criarUsuario();

        Mockito.when(usuarioRepository.findByLogin(Mockito.any())).thenReturn(Optional.of(u));

        Usuario usuario = service.findByLogin(u.getLogin());

        Assertions.assertThat(usuario).isNotNull();
        Assertions.assertThat(usuario.getId()).isEqualTo(u.getId());
        Assertions.assertThat(usuario.getLogin()).isEqualTo(u.getLogin());
        Assertions.assertThat(usuario.getPassword()).isEqualTo(u.getPassword());
    }

    @Test
    void deveLancarExcecaoAoNaoAcharUsuarioPeloLogin(){
        Mockito.when(usuarioRepository.findByLogin(Mockito.any())).thenReturn(Optional.empty());

        Throwable erro = Assertions.catchThrowable(() -> service.findByLogin("Pablo"));

        Assertions.assertThat(erro).isInstanceOf(UsuarioNaoEncontradoException.class).hasMessage("Usuário não encontrado!");

        Mockito.verify(usuarioRepository, Mockito.times(1)).findByLogin("Pablo");
    }

    @Test
    void deveAcharUsuarioPeloEmail(){
        Usuario u = criarUsuario();

        Mockito.when(usuarioRepository.findByEmail(Mockito.any())).thenReturn(Optional.of(u));

        Usuario usuario = service.findByEmail(u.getEmail());

        Assertions.assertThat(usuario).isNotNull();
        Assertions.assertThat(usuario.getId()).isEqualTo(u.getId());
        Assertions.assertThat(usuario.getEmail()).isEqualTo(u.getEmail());
        Assertions.assertThat(usuario.getPassword()).isEqualTo(u.getPassword());
    }

    @Test
    void deveLancarExcecaoAoNaoAcharUsuarioPeloEmail(){
        Mockito.when(usuarioRepository.findByEmail(Mockito.any())).thenReturn(Optional.empty());

        Throwable erro = Assertions.catchThrowable(() -> service.findByEmail("email@gmail.com"));

        Assertions.assertThat(erro).isInstanceOf(UsuarioNaoEncontradoException.class).hasMessage("Usuário não encontrado!");

        Mockito.verify(usuarioRepository, Mockito.times(1)).findByEmail("email@gmail.com");
    }

    @Test
    void deveRegistrarUsuario(){
        Usuario u = criarUsuario();
        UsuarioPostDTO postDTO = new UsuarioPostDTO(u.getLogin(), u.getEmail(), u.getPassword());

        Mockito.doNothing().when(validator).usuarioValidator(postDTO.login(), postDTO.email());
        Mockito.when(mapper.toEntity(postDTO)).thenReturn(u);
        Mockito.when(encoder.encode(postDTO.password())).thenReturn("senhaCriptografada");
        Mockito.when(usuarioRepository.save(Mockito.any())).thenReturn(u);

        service.registerUser(postDTO);

        ArgumentCaptor<Usuario> captor = ArgumentCaptor.forClass(Usuario.class);
        Mockito.verify(usuarioRepository).save(captor.capture());
        Mockito.verify(validator, Mockito.times(1)).usuarioValidator(postDTO.login(), postDTO.email());

        Assertions.assertThat(captor.getValue().getPassword()).isEqualTo("senhaCriptografada");
        Assertions.assertThat(captor.getValue().getEmail()).isEqualTo(u.getEmail());
        Assertions.assertThat(captor.getValue().getLogin()).isEqualTo(u.getLogin());
    }

    @Test
    void deveLogarUsuario(){
        Usuario u = criarUsuario();
        UsuarioLoginDTO loginDTO = new UsuarioLoginDTO(u.getEmail(), u.getPassword());

        Mockito.when(usuarioRepository.findByEmail(Mockito.any())).thenReturn(Optional.of(u));
        Mockito.when(encoder.matches(Mockito.any(), Mockito.any())).thenReturn(true);
        Mockito.when(jwtTokenService.generateToken(u)).thenReturn("token");

        String token = service.loginUser(loginDTO);

        Assertions.assertThat(token).isEqualTo("token");

        Mockito.verify(usuarioRepository).findByEmail(loginDTO.email());
        Mockito.verify(encoder).matches(loginDTO.password(), u.getPassword());
        Mockito.verify(jwtTokenService).generateToken(u);
    }

    @Test
    void deveLancarExcecaoQuandoComparacaoDeSenhaRetornaFalse(){
        Usuario u = criarUsuario();
        UsuarioLoginDTO loginDTO = new UsuarioLoginDTO(u.getEmail(), u.getPassword());

        Mockito.when(usuarioRepository.findByEmail(Mockito.any())).thenReturn(Optional.of(u));
        Mockito.when(encoder.matches(Mockito.any(), Mockito.any())).thenReturn(false);

        Throwable erro = Assertions.catchThrowable(() -> service.loginUser(loginDTO));

        Assertions.assertThat(erro).isInstanceOf(BadCredentialsException.class).hasMessage("Credencial errada!");

        Mockito.verify(jwtTokenService, Mockito.never()).generateToken(Mockito.any());
    }
}
