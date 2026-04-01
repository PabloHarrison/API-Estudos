package com.example.DBEstudosAPI.controller;

import com.example.DBEstudosAPI.configuration.SecurityConfigurationTest;
import com.example.DBEstudosAPI.dto.CategoriaResponseDTO;
import com.example.DBEstudosAPI.dto.RegistroPatchDTO;
import com.example.DBEstudosAPI.dto.RegistroPostDTO;
import com.example.DBEstudosAPI.dto.RegistroResponseDTO;
import com.example.DBEstudosAPI.entities.Categoria;
import com.example.DBEstudosAPI.entities.Registro;
import com.example.DBEstudosAPI.exceptions.CategoriaNaoEncontradaException;
import com.example.DBEstudosAPI.exceptions.RegistroNaoEncontradoException;
import com.example.DBEstudosAPI.service.RegistroService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.MediaType;
import org.springframework.security.oauth2.server.resource.InvalidBearerTokenException;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@WebMvcTest(RegistroController.class)
@Import(SecurityConfigurationTest.class)
public class RegistroControllerTest {

    @Autowired
    MockMvc mvc;
    @Autowired
    ObjectMapper mapper;
    @MockitoBean
    RegistroService service;

    private Categoria criarCategoria() {
        Categoria c = new Categoria();
        c.setId(UUID.randomUUID());
        c.setNomeCategoria("Java");
        return c;
    }

    private Registro criarRegistro(Categoria c) {
        Registro r = new Registro();
        r.setId(UUID.randomUUID());
        r.setData(LocalDate.of(2026, 4, 1));
        r.setHorasEstudadas(5);
        r.setCategorias(Set.of(c));
        return r;
    }

    private RegistroResponseDTO criarResponseDTO(Registro r, Categoria c) {
        CategoriaResponseDTO categoriaResponseDTO = new CategoriaResponseDTO(c.getId(), c.getNomeCategoria());
        return new RegistroResponseDTO(
                r.getId(),
                r.getData(),
                r.getHorasEstudadas(),
                r.getAnotacao(),
                r.getResumo(),
                r.getPlanejamento(),
                Set.of(categoriaResponseDTO));
    }

    @Test
    void deveSalvarRegistro() throws Exception {
        Categoria c = criarCategoria();
        UUID cId = c.getId();
        Registro r = criarRegistro(c);
        RegistroPostDTO dto = new RegistroPostDTO(r.getData(), r.getHorasEstudadas(), r.getAnotacao(), r.getResumo(), r.getPlanejamento(), Set.of(cId));
        RegistroResponseDTO responseDTO = criarResponseDTO(r, c);

        Mockito.when(service.save(Mockito.any())).thenReturn(responseDTO);

        String json = mapper.writeValueAsString(dto);

        mvc.perform(MockMvcRequestBuilders.post("/registros")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(MockMvcResultMatchers.status().isCreated());

        Mockito.verify(service).save(Mockito.any());
    }

    @Test
    void deveImpedirSalvarRegistroPor400BadRequest() throws Exception {
        Categoria c = criarCategoria();
        UUID cId = c.getId();
        Registro r = criarRegistro(c);
        RegistroPostDTO dto = new RegistroPostDTO(null, r.getHorasEstudadas(), r.getAnotacao(), r.getResumo(), r.getPlanejamento(), Set.of(cId));
        RegistroResponseDTO responseDTO = criarResponseDTO(r, c);

        Mockito.when(service.save(Mockito.any())).thenReturn(responseDTO);

        String json = mapper.writeValueAsString(dto);

        mvc.perform(MockMvcRequestBuilders.post("/registros")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(MockMvcResultMatchers.status().isBadRequest());

        Mockito.verify(service, Mockito.never()).save(Mockito.any());
    }

    @Test
    void deveImpedirSalvarRegistroPor401Unauthorized() throws Exception {
        Categoria c = criarCategoria();
        UUID cId = c.getId();
        Registro r = criarRegistro(c);
        RegistroPostDTO dto = new RegistroPostDTO(r.getData(), r.getHorasEstudadas(), r.getAnotacao(), r.getResumo(), r.getPlanejamento(), Set.of(cId));

        Mockito.doThrow(new InvalidBearerTokenException("Sessão inválida ou expirada.")).when(service).save(Mockito.any());

        String json = mapper.writeValueAsString(dto);

        mvc.perform(MockMvcRequestBuilders.post("/registros")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(MockMvcResultMatchers.status().isUnauthorized());

        Mockito.verify(service).save(Mockito.any());
    }

    @Test
    void deveImpedirSalvarRegistroPor404NotFound() throws Exception {
        Categoria c = criarCategoria();
        UUID cId = c.getId();
        Registro r = criarRegistro(c);
        RegistroPostDTO dto = new RegistroPostDTO(r.getData(), r.getHorasEstudadas(), r.getAnotacao(), r.getResumo(), r.getPlanejamento(), Set.of(cId));

        Mockito.doThrow(new CategoriaNaoEncontradaException("Uma ou mais categorias não existem.")).when(service).save(Mockito.any());

        String json = mapper.writeValueAsString(dto);

        mvc.perform(MockMvcRequestBuilders.post("/registros")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(MockMvcResultMatchers.status().isNotFound());

        Mockito.verify(service).save(Mockito.any());
    }

    @Test
    void deveBuscarRegistroPorId() throws Exception {
        Categoria c = criarCategoria();
        UUID cId = c.getId();
        Registro r = criarRegistro(c);
        RegistroResponseDTO responseDTO = criarResponseDTO(r, c);

        Mockito.when(service.findById(r.getId())).thenReturn(responseDTO);

        mvc.perform(MockMvcRequestBuilders.get("/registros/{id}", r.getId()))
                .andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Test
    void deveImpedirBuscarRegistroPor401Unauthorized() throws Exception {
        Categoria c = criarCategoria();
        Registro r = criarRegistro(c);

        Mockito.doThrow(new InvalidBearerTokenException("Sessão inválida ou expirada.")).when(service).findById(r.getId());

        mvc.perform(MockMvcRequestBuilders.get("/registros/{id}", r.getId()))
                .andExpect(MockMvcResultMatchers.status().isUnauthorized());

        Mockito.verify(service).findById(Mockito.any());
    }

    @Test
    void deveImpedirBuscarRegistroPor404NotFound() throws Exception {
        Categoria c = criarCategoria();
        Registro r = criarRegistro(c);

        Mockito.doThrow(new RegistroNaoEncontradoException("Registro não encontrado.")).when(service).findById(r.getId());

        mvc.perform(MockMvcRequestBuilders.get("/registros/{id}", r.getId()))
                .andExpect(MockMvcResultMatchers.status().isNotFound());

        Mockito.verify(service).findById(Mockito.any());
    }

    @Test
    void devePesquisarRegistro() throws Exception {
        Categoria c = criarCategoria();
        Registro r = criarRegistro(c);
        List<RegistroResponseDTO> listResponseDTO = List.of(criarResponseDTO(r, c));
        Page<RegistroResponseDTO> dtoPage = new PageImpl<>(listResponseDTO);

        Mockito.when(service.search(
                r.getData().getYear(),
                r.getData().getMonthValue(),
                r.getData().getDayOfMonth(),
                c.getNomeCategoria(),
                1,
                10,
                0,
                10)).thenReturn(dtoPage);

        mvc.perform(MockMvcRequestBuilders.get("/registros")
                        .param("ano", "2026")
                        .param("mes", "4")
                        .param("dia", "1")
                        .param("nome-categoria", "Java")
                        .param("min", "1")
                        .param("max", "10")
                        .param("pagina", "0")
                        .param("tamanho-paginas", "10"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.content[*].data").value("2026-04-01"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.content[*].categorias[*].nomeCategoria").value("Java"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.content[*].horasEstudadas").value(5))
                .andExpect(MockMvcResultMatchers.jsonPath("$.content").isNotEmpty())
                .andExpect(MockMvcResultMatchers.jsonPath("$.content.length()").value(1));
    }

    @Test
    void deveImpedirPesquisarRegistroPor401Unauthorized() throws Exception {
        Categoria c = criarCategoria();
        Registro r = criarRegistro(c);

        Mockito.doThrow(new InvalidBearerTokenException("Sessão inválida ou expirada.")).when(service).search(
                r.getData().getYear(),
                r.getData().getMonthValue(),
                r.getData().getDayOfMonth(),
                c.getNomeCategoria(),
                1,
                10,
                0,
                10);

        mvc.perform(MockMvcRequestBuilders.get("/registros")
                        .param("ano", "2026")
                        .param("mes", "4")
                        .param("dia", "1")
                        .param("nome-categoria", "Java")
                        .param("min", "1")
                        .param("max", "10")
                        .param("pagina", "0")
                        .param("tamanho-paginas", "10"))
                .andExpect(MockMvcResultMatchers.status().isUnauthorized());
    }

    @Test
    void deveDeletarRegistro() throws Exception {
        Categoria c = criarCategoria();
        Registro r = criarRegistro(c);

        Mockito.doNothing().when(service).delete(r.getId());

        mvc.perform(MockMvcRequestBuilders.delete("/registros/{id}", r.getId()))
                        .andExpect(MockMvcResultMatchers.status().isNoContent());

        Mockito.verify(service).delete(r.getId());
    }
    @Test
    void deveImpedirDeletarRegistroPor401Unauthorized() throws Exception {
        Categoria c = criarCategoria();
        Registro r = criarRegistro(c);

        Mockito.doThrow(new InvalidBearerTokenException("Sessão inválida ou expirada.")).when(service).delete(r.getId());

        mvc.perform(MockMvcRequestBuilders.delete("/registros/{id}", r.getId()))
                .andExpect(MockMvcResultMatchers.status().isUnauthorized());

        Mockito.verify(service).delete(r.getId());
    }

    @Test
    void deveImpedirDeletarRegistroPor404NotFound() throws Exception {
        Categoria c = criarCategoria();
        Registro r = criarRegistro(c);

        Mockito.doThrow(new RegistroNaoEncontradoException("Registro não encontrado")).when(service).delete(r.getId());

        mvc.perform(MockMvcRequestBuilders.delete("/registros/{id}", r.getId()))
                .andExpect(MockMvcResultMatchers.status().isNotFound());

        Mockito.verify(service).delete(r.getId());
    }

    @Test
    void deveAtualizarRegistro() throws Exception {
        Categoria c = criarCategoria();
        UUID cId = c.getId();
        Registro r = criarRegistro(c);
        String id = String.valueOf(r.getId());
        RegistroPatchDTO patchDTO = new RegistroPatchDTO(r.getData(), r.getHorasEstudadas(), r.getAnotacao(), r.getResumo(), r.getPlanejamento(), Set.of(cId));
        RegistroResponseDTO responseDTO = criarResponseDTO(r, c);

        Mockito.when(service.update(Mockito.eq(id), Mockito.any())).thenReturn(responseDTO);

        String json = mapper.writeValueAsString(patchDTO);

        mvc.perform(MockMvcRequestBuilders.patch("/registros/{id}", id)
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.data").value("2026-04-01"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.categorias").isNotEmpty())
                .andExpect(MockMvcResultMatchers.jsonPath("$.categorias[*].nomeCategoria").value("Java"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.horasEstudadas").value(5));

        Mockito.verify(service).update(Mockito.eq(id), Mockito.any());
    }

    @Test
    void deveImpedirAtualizarRegistroPor400BadRequest() throws Exception {
        Categoria c = criarCategoria();
        Registro r = criarRegistro(c);
        String id = String.valueOf(r.getId());
        RegistroPatchDTO patchDTO = new RegistroPatchDTO(LocalDate.now().plusDays(1), null, null, null, null, null);
        RegistroResponseDTO responseDTO = criarResponseDTO(r, c);

        Mockito.when(service.update(Mockito.eq(id), Mockito.any())).thenReturn(responseDTO);

        String json = mapper.writeValueAsString(patchDTO);

        mvc.perform(MockMvcRequestBuilders.patch("/registros/{id}", id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(MockMvcResultMatchers.status().isBadRequest());

        Mockito.verify(service, Mockito.never()).update(Mockito.eq(id), Mockito.any());
    }

    @Test
    void deveImpedirAtualizarRegistroPor401Unauthorized() throws Exception {
        Categoria c = criarCategoria();
        UUID cId = c.getId();
        Registro r = criarRegistro(c);
        String id = String.valueOf(r.getId());
        RegistroPatchDTO patchDTO = new RegistroPatchDTO(r.getData(), r.getHorasEstudadas(), r.getAnotacao(), r.getResumo(), r.getPlanejamento(), Set.of(cId));

        Mockito.doThrow(new InvalidBearerTokenException("Sessão inválida ou expirada.")).when(service).update(Mockito.eq(id), Mockito.any());

        String json = mapper.writeValueAsString(patchDTO);

        mvc.perform(MockMvcRequestBuilders.patch("/registros/{id}", id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(MockMvcResultMatchers.status().isUnauthorized());

        Mockito.verify(service).update(Mockito.eq(id), Mockito.any());
    }

    @Test
    void deveImpedirAtualizarRegistroPor404NotFound() throws Exception {
        Categoria c = criarCategoria();
        UUID cId = c.getId();
        Registro r = criarRegistro(c);
        String id = String.valueOf(r.getId());
        RegistroPatchDTO patchDTO = new RegistroPatchDTO(r.getData(), r.getHorasEstudadas(), r.getAnotacao(), r.getResumo(), r.getPlanejamento(), Set.of(cId));

        Mockito.doThrow(new CategoriaNaoEncontradaException("Categoria não encontrada.")).when(service).update(Mockito.eq(id), Mockito.any());

        String json = mapper.writeValueAsString(patchDTO);

        mvc.perform(MockMvcRequestBuilders.patch("/registros/{id}", id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(MockMvcResultMatchers.status().isNotFound());

        Mockito.verify(service).update(Mockito.eq(id), Mockito.any());
    }

}
