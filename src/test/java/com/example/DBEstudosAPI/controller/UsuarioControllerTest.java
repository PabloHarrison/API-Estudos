package com.example.DBEstudosAPI.controller;

import com.example.DBEstudosAPI.configuration.SecurityConfigurationTest;
import com.example.DBEstudosAPI.dto.RefreshTokenRequestDTO;
import com.example.DBEstudosAPI.dto.TokenResponseDTO;
import com.example.DBEstudosAPI.dto.UsuarioLoginDTO;
import com.example.DBEstudosAPI.dto.UsuarioPostDTO;
import com.example.DBEstudosAPI.entities.Usuario;
import com.example.DBEstudosAPI.enums.Roles;
import com.example.DBEstudosAPI.exceptions.EmailCadastradoException;
import com.example.DBEstudosAPI.exceptions.RefreshTokenInvalidoException;
import com.example.DBEstudosAPI.service.RefreshTokenService;
import com.example.DBEstudosAPI.service.UsuarioService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

@WebMvcTest(UsuarioController.class)
@Import(SecurityConfigurationTest.class)
public class UsuarioControllerTest {

    @Autowired
    MockMvc mvc;
    @Autowired
    ObjectMapper mapper;
    @MockitoBean
    UsuarioService service;
    @MockitoBean
    RefreshTokenService refreshTokenService;

    private Usuario criarUsuario(){
        Usuario u = new Usuario();
        u.setLogin("pablo");
        u.setEmail("email@gmail.com");
        u.setPassword("123");
        u.setRoles(Roles.ADMIN);
        return u;
    }
    @Test
    void deveRegistrarUsuario() throws Exception{
        Usuario u = criarUsuario();
        UsuarioPostDTO dto = new UsuarioPostDTO(u.getLogin(),u.getEmail(), u.getPassword());

        Mockito.doNothing().when(service).registerUser(Mockito.any());

        String json = mapper.writeValueAsString(dto);

        mvc.perform(MockMvcRequestBuilders.post("/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
                .andExpect(MockMvcResultMatchers.status().isCreated());
    }

    @Test
    void deveImpedirORegistroUsuarioPor400BadRequest() throws Exception{
        Usuario u = criarUsuario();
        UsuarioPostDTO dto = new UsuarioPostDTO(u.getLogin(), null, u.getPassword());

        Mockito.doNothing().when(service).registerUser(Mockito.any());

        String json = mapper.writeValueAsString(dto);

        mvc.perform(MockMvcRequestBuilders.post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(MockMvcResultMatchers.status().isBadRequest());
    }

    @Test
    void deveImpedirORegistroUsuarioPor409Conflict() throws Exception{
        Usuario u = criarUsuario();
        UsuarioPostDTO dto = new UsuarioPostDTO(u.getLogin(), u.getEmail(), u.getPassword());

        Mockito.doThrow(new EmailCadastradoException("Email já cadastrado.")).when(service).registerUser(Mockito.any());

        String json = mapper.writeValueAsString(dto);

        mvc.perform(MockMvcRequestBuilders.post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(MockMvcResultMatchers.status().isConflict());
    }

    @Test
    void deveLogarUsuario() throws Exception{
        Usuario u = criarUsuario();
        UsuarioLoginDTO dto = new UsuarioLoginDTO(u.getEmail(), u.getPassword());

        Mockito.when(service.loginUser(Mockito.any())).thenReturn(new TokenResponseDTO("token", "refreshToken"));

        String json = mapper.writeValueAsString(dto);

        mvc.perform(MockMvcRequestBuilders.post("/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.accessToken").value("token"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.refreshToken").value("refreshToken"));
    }

    @Test
    void deveImpedirLogarUsuarioPor400BadRequest() throws Exception{
        Usuario u = criarUsuario();
        UsuarioLoginDTO dto = new UsuarioLoginDTO(u.getEmail(), null);

        Mockito.when(service.loginUser(Mockito.any())).thenReturn(new TokenResponseDTO("token", "refreshToken"));

        String json = mapper.writeValueAsString(dto);

        mvc.perform(MockMvcRequestBuilders.post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(MockMvcResultMatchers.status().isBadRequest());
    }

    @Test
    void deveImpedirLogarUsuarioPor401Unauthorized() throws Exception{
        Usuario u = criarUsuario();
        UsuarioLoginDTO dto = new UsuarioLoginDTO(u.getEmail(), u.getPassword());

        Mockito.doThrow(new BadCredentialsException("Credentiais inválidas.")).when(service).loginUser(Mockito.any());

        String json = mapper.writeValueAsString(dto);

        mvc.perform(MockMvcRequestBuilders.post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(MockMvcResultMatchers.status().isUnauthorized());
    }

    @Test
    void deveRenovarTokens() throws Exception {
        RefreshTokenRequestDTO requestDTO = new RefreshTokenRequestDTO("refreshToken");
        Mockito.when(refreshTokenService.refresh(Mockito.any())).thenReturn(new TokenResponseDTO("token", "refreshToken"));

        String json = mapper.writeValueAsString(requestDTO);

        mvc.perform(MockMvcRequestBuilders.post("/auth/refresh")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.accessToken").value("token"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.refreshToken").value("refreshToken"));
    }

    @Test
    void deveImpedirRenovarTokensPor400BadRequest() throws Exception{
        RefreshTokenRequestDTO requestDTO = new RefreshTokenRequestDTO(null);
        Mockito.when(refreshTokenService.refresh(Mockito.any())).thenReturn(new TokenResponseDTO("token", "refreshToken"));

        String json = mapper.writeValueAsString(requestDTO);

        mvc.perform(MockMvcRequestBuilders.post("/auth/refresh")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(MockMvcResultMatchers.status().isBadRequest());
    }

    @Test
    void deveImpedirRenovarTokenPor401Unauthorized() throws Exception{
        RefreshTokenRequestDTO requestDTO = new RefreshTokenRequestDTO("refreshToken");
        Mockito.doThrow(new RefreshTokenInvalidoException("Refresh Token inválido!")).when(refreshTokenService).refresh(Mockito.any());

        String json = mapper.writeValueAsString(requestDTO);

        mvc.perform(MockMvcRequestBuilders.post("/auth/refresh")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(MockMvcResultMatchers.status().isUnauthorized());
    }
}
