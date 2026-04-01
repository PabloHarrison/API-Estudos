package com.example.DBEstudosAPI.controller;

import com.example.DBEstudosAPI.configuration.SecurityConfigurationTest;
import com.example.DBEstudosAPI.dto.CategoriaPatchDTO;
import com.example.DBEstudosAPI.dto.CategoriaPostDTO;
import com.example.DBEstudosAPI.dto.CategoriaResponseDTO;
import com.example.DBEstudosAPI.entities.Categoria;
import com.example.DBEstudosAPI.entities.Usuario;
import com.example.DBEstudosAPI.exceptions.CategoriaEmUsoException;
import com.example.DBEstudosAPI.exceptions.CategoriaNaoEncontradaException;
import com.example.DBEstudosAPI.service.CategoriaService;
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
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@WebMvcTest(CategoriaController.class)
@Import(SecurityConfigurationTest.class)
public class CategoriaControllerTest {

    @Autowired
    MockMvc mvc;
    @Autowired
    ObjectMapper mapper;
    @MockitoBean
    CategoriaService service;

    private Usuario criarUsuario() {
        return new Usuario();
    }

    private Categoria criarCategoria(Usuario u) {
        Categoria c = new Categoria();
        c.setNomeCategoria("Java");
        c.setUsuario(u);
        return c;
    }

    @Test
    void deveSalvarCategoria() throws Exception {
        Usuario u = criarUsuario();
        Categoria c = criarCategoria(u);
        CategoriaPostDTO dto = new CategoriaPostDTO(c.getNomeCategoria());
        CategoriaResponseDTO responseDTO = new CategoriaResponseDTO(c.getId(), c.getNomeCategoria());

        Mockito.when(service.save(Mockito.any())).thenReturn(responseDTO);

        String json = mapper.writeValueAsString(dto);

        mvc.perform(MockMvcRequestBuilders.post("/categorias")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(MockMvcResultMatchers.status().isCreated());

        Mockito.verify(service).save(Mockito.any());
    }

    @Test
    void deveImpedirDeSalvarCategoriaPor400BadRequest() throws Exception {
        Usuario u = criarUsuario();
        Categoria c = criarCategoria(u);
        CategoriaPostDTO dto = new CategoriaPostDTO(null);
        CategoriaResponseDTO responseDTO = new CategoriaResponseDTO(c.getId(), c.getNomeCategoria());

        Mockito.when(service.save(Mockito.any())).thenReturn(responseDTO);

        String json = mapper.writeValueAsString(dto);

        mvc.perform(MockMvcRequestBuilders.post("/categorias")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(MockMvcResultMatchers.status().isBadRequest());

        Mockito.verify(service, Mockito.never()).save(Mockito.any());
    }

    @Test
    void deveImpedirDeSalvarCategoriaPor401Unauthorized() throws Exception {
        Usuario u = criarUsuario();
        Categoria c = criarCategoria(u);
        CategoriaPostDTO dto = new CategoriaPostDTO(c.getNomeCategoria());

        Mockito.doThrow(new BadCredentialsException("Credentiais inválidas.")).when(service).save(Mockito.any());

        String json = mapper.writeValueAsString(dto);

        mvc.perform(MockMvcRequestBuilders.post("/categorias")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(MockMvcResultMatchers.status().isUnauthorized());

        Mockito.verify(service).save(Mockito.any());
    }

    @Test
    void deveBuscarCategoriaPorId() throws Exception {
        Usuario u = criarUsuario();
        Categoria c = criarCategoria(u);
        UUID id = UUID.randomUUID();

        CategoriaResponseDTO responseDTO = new CategoriaResponseDTO(id, c.getNomeCategoria());

        Mockito.when(service.findById(id)).thenReturn(responseDTO);

        mvc.perform(MockMvcRequestBuilders.get("/categorias/{id}", id))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.nomeCategoria").value("Java"));
    }

    @Test
    void deveImpedirBuscarPorIdPor401Unauthorized() throws Exception {
        UUID id = UUID.randomUUID();

        Mockito.doThrow(new BadCredentialsException("Sessão inválida ou expirada")).when(service).findById(id);

        mvc.perform(MockMvcRequestBuilders.get("/categorias/{id}", id))
                .andExpect(MockMvcResultMatchers.status().isUnauthorized());

        Mockito.verify(service).findById(id);
    }

    @Test
    void deveImpedirBuscarPorIdPor404NotFound() throws Exception {
        UUID id = UUID.randomUUID();

        Mockito.doThrow(new CategoriaNaoEncontradaException("Categoria não encontrado.")).when(service).findById(id);

        mvc.perform(MockMvcRequestBuilders.get("/categorias/{id}", id))
                .andExpect(MockMvcResultMatchers.status().isNotFound());

        Mockito.verify(service).findById(id);
    }

    @Test
    void devePesquisarCategoria() throws Exception {
        Usuario u = criarUsuario();
        Categoria c = criarCategoria(u);
        UUID id = UUID.randomUUID();
        CategoriaResponseDTO responseDTO = new CategoriaResponseDTO(id, c.getNomeCategoria());
        Set<CategoriaResponseDTO> categorias = new HashSet<>();
        categorias.add(responseDTO);

        Mockito.when(service.search(c.getNomeCategoria())).thenReturn(categorias);

        mvc.perform(MockMvcRequestBuilders.get("/categorias").param("nome-categoria", "Java"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$[*].nomeCategoria").value("Java"));

        Mockito.verify(service).search(c.getNomeCategoria());
    }

    @Test
    void deveImpedirPesquisaPor401Unauthorized() throws Exception {
        Usuario u = criarUsuario();
        Categoria c = criarCategoria(u);

        Mockito.doThrow(new BadCredentialsException("Sessão inválida ou expirada")).when(service).search(c.getNomeCategoria());

        mvc.perform(MockMvcRequestBuilders.get("/categorias").param("nome-categoria", "Java"))
                .andExpect(MockMvcResultMatchers.status().isUnauthorized());

        Mockito.verify(service).search(c.getNomeCategoria());
    }

    @Test
    void deveDeletarCategoria() throws Exception {
        UUID id = UUID.randomUUID();

        Mockito.doNothing().when(service).delete(id);

        mvc.perform(MockMvcRequestBuilders.delete("/categorias/{id}", id))
                .andExpect(MockMvcResultMatchers.status().isNoContent());

        Mockito.verify(service).delete(id);
    }

    @Test
    void deveImpedirDeletarCategoriaPor401Unauthorized() throws Exception {
        UUID id = UUID.randomUUID();

        Mockito.doThrow(new BadCredentialsException("Sessão inválida ou expirada")).when(service).delete(id);

        mvc.perform(MockMvcRequestBuilders.delete("/categorias/{id}", id))
                .andExpect(MockMvcResultMatchers.status().isUnauthorized());

        Mockito.verify(service).delete(id);
    }

    @Test
    void deveImpedirDeletarCategoriaPor404NotFound() throws Exception {
        UUID id = UUID.randomUUID();

        Mockito.doThrow(new CategoriaNaoEncontradaException("Categoria não encontrado.")).when(service).delete(id);

        mvc.perform(MockMvcRequestBuilders.delete("/categorias/{id}", id))
                .andExpect(MockMvcResultMatchers.status().isNotFound());

        Mockito.verify(service).delete(id);
    }

    @Test
    void deveImpedirDeletarCategoriaPor409Conflit() throws Exception {
        UUID id = UUID.randomUUID();

        Mockito.doThrow(new CategoriaEmUsoException("Não é possivel deletar uma categoria que esteja sendo usada por um registro.")).when(service).delete(id);

        mvc.perform(MockMvcRequestBuilders.delete("/categorias/{id}", id))
                .andExpect(MockMvcResultMatchers.status().isConflict());

        Mockito.verify(service).delete(id);
    }

    @Test
    void deveAtualizarCategoria() throws Exception {
        UUID id = UUID.randomUUID();
        CategoriaPatchDTO dto = new CategoriaPatchDTO("Spring");
        CategoriaResponseDTO responseDTO = new CategoriaResponseDTO(id, dto.nomeCategoria());

        Mockito.when(service.update(Mockito.eq(id), Mockito.any())).thenReturn(responseDTO);

        String json = mapper.writeValueAsString(dto);

        mvc.perform(MockMvcRequestBuilders.patch("/categorias/{id}", id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.nomeCategoria").value("Spring"));
    }

    @Test
    void deveImpedirAtualizarCategoriaPor400BadRequest() throws Exception {
        UUID id = UUID.randomUUID();
        CategoriaPatchDTO dto = new CategoriaPatchDTO(null);
        CategoriaResponseDTO responseDTO = new CategoriaResponseDTO(id, dto.nomeCategoria());

        Mockito.when(service.update(Mockito.eq(id), Mockito.any())).thenReturn(responseDTO);

        String json = mapper.writeValueAsString(dto);

        mvc.perform(MockMvcRequestBuilders.patch("/categorias/{id}", id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(MockMvcResultMatchers.status().isBadRequest());
    }

    @Test
    void deveImpedirAtualizarCategoriaPor401Unauthorized() throws Exception {
        UUID id = UUID.randomUUID();
        CategoriaPatchDTO dto = new CategoriaPatchDTO("Spring");

        Mockito.doThrow(new BadCredentialsException("Sessão inválida ou expirada")).when(service).update(Mockito.eq(id), Mockito.any());

        String json = mapper.writeValueAsString(dto);

        mvc.perform(MockMvcRequestBuilders.patch("/categorias/{id}", id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(MockMvcResultMatchers.status().isUnauthorized());

        Mockito.verify(service).update(Mockito.eq(id), Mockito.any());
    }

    @Test
    void deveImpedirAtualizarCategoriaPor404NotFound() throws Exception {
        UUID id = UUID.randomUUID();
        CategoriaPatchDTO dto = new CategoriaPatchDTO("Spring");

        Mockito.doThrow(new CategoriaNaoEncontradaException("Categoria não encontrado.")).when(service).update(Mockito.eq(id), Mockito.any());

        String json = mapper.writeValueAsString(dto);

        mvc.perform(MockMvcRequestBuilders.patch("/categorias/{id}", id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(MockMvcResultMatchers.status().isNotFound());

        Mockito.verify(service).update(Mockito.eq(id), Mockito.any());
    }
}
