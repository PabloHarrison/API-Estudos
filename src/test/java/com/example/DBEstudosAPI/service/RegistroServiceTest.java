package com.example.DBEstudosAPI.service;

import com.example.DBEstudosAPI.dto.CategoriaResponseDTO;
import com.example.DBEstudosAPI.dto.RegistroPatchDTO;
import com.example.DBEstudosAPI.dto.RegistroPostDTO;
import com.example.DBEstudosAPI.dto.RegistroResponseDTO;
import com.example.DBEstudosAPI.entities.Categoria;
import com.example.DBEstudosAPI.entities.Registro;
import com.example.DBEstudosAPI.entities.Usuario;
import com.example.DBEstudosAPI.exceptions.CategoriaNaoEncontradaException;
import com.example.DBEstudosAPI.exceptions.RegistroNaoEncontradoException;
import com.example.DBEstudosAPI.exceptions.UsuarioNaoEncontradoException;
import com.example.DBEstudosAPI.mappers.RegistroMapper;
import com.example.DBEstudosAPI.repository.CategoriaRepository;
import com.example.DBEstudosAPI.repository.RegistroRepository;
import com.example.DBEstudosAPI.repository.UsuarioRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

@ExtendWith(MockitoExtension.class)
public class RegistroServiceTest {

    @InjectMocks
    RegistroService service;

    @Mock
    RegistroRepository registroRepository;
    @Mock
    CategoriaRepository categoriaRepository;
    @Mock
    RegistroMapper mapper;
    @Mock
    UsuarioRepository usuarioRepository;
    @Mock
    SecurityContext securityContext;
    @Mock
    Authentication authentication;

    private Categoria criarCategoria(Usuario u){
        Categoria c = new Categoria();
        c.setNomeCategoria("Java");
        c.setId(UUID.randomUUID());
        c.setUsuario(u);
        return c;
    }
    private Usuario criarUsuario() {
        Usuario u = new Usuario();
        u.setId(UUID.fromString("7dce5f33-8375-4514-85c8-9968681c4815"));
        return u;
    }
    private Registro criarRegistro(Categoria c, Usuario u){
        Registro r = new Registro();
        r.setId(UUID.randomUUID());
        r.setData(LocalDate.now());
        r.setHorasEstudadas(10);
        r.setCategorias(Set.of(c));
        r.setUsuario(u);
        return r;
    }
    private void mockBase(Registro r, Page<Registro> page, Categoria c) {
        CategoriaResponseDTO cResponseDTO = new CategoriaResponseDTO(c.getId(), c.getNomeCategoria());

        Mockito.when(authentication.getName()).thenReturn("7dce5f33-8375-4514-85c8-9968681c4815");
        Mockito.when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);
        Mockito.when(registroRepository.findAll(Mockito.<Specification<Registro>>any(), Mockito.<Pageable>any())).thenReturn(page);
        Mockito.when(mapper.toDTO(Mockito.any()))
                .thenReturn(new RegistroResponseDTO(r.getId(), r.getData(), r.getHorasEstudadas(), "", "", "", Set.of(cResponseDTO)));
    }

    @Test
    void deveSalvarRegistro(){
        Usuario u = criarUsuario();
        Categoria c = criarCategoria(u);
        UUID cId = c.getId();
        RegistroPostDTO postDTO = new RegistroPostDTO(LocalDate.now(), 10, "", "", "", Set.of(cId));
        Registro r = criarRegistro(c, u);
        CategoriaResponseDTO cResponseDTO = new CategoriaResponseDTO(c.getId(), c.getNomeCategoria());

        Mockito.when(mapper.toEntity(postDTO)).thenReturn(r);
        Mockito.when(authentication.getName()).thenReturn("7dce5f33-8375-4514-85c8-9968681c4815");
        Mockito.when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);
        Mockito.when(usuarioRepository.findById(Mockito.any())).thenReturn(Optional.of(u));
        Mockito.when(categoriaRepository.findAllByIdInAndUsuarioId(Mockito.eq(Set.of(cId)), Mockito.any())).thenReturn(Set.of(c));
        Mockito.when(registroRepository.save(Mockito.any())).thenReturn(r);
        Mockito.when(mapper.toDTO(r))
                .thenReturn(new RegistroResponseDTO(r.getId(),
                        r.getData(), 10, "", "", "", Set.of(cResponseDTO)));

        RegistroResponseDTO rSalvo = service.save(postDTO);

        Assertions.assertThat(rSalvo).isNotNull();
        Assertions.assertThat(rSalvo.data()).isEqualTo(r.getData());
        Assertions.assertThat(rSalvo.horasEstudadas()).isEqualTo(10);
        Assertions.assertThat(rSalvo.id()).isEqualTo(r.getId());
        Assertions.assertThat(rSalvo.categorias()).extracting(CategoriaResponseDTO::id).contains(c.getId());

        Mockito.verify(registroRepository).save(r);
    }

    @Test
    void deveLancarExcecaoQuandoUsuarioNaoEncontradoAoSalvarRegistro(){
        Usuario u = criarUsuario();
        Categoria c = criarCategoria(u);
        UUID cId = c.getId();
        RegistroPostDTO postDTO = new RegistroPostDTO(LocalDate.now(), 10, "", "", "", Set.of(cId));
        Registro r = criarRegistro(c, u);

        Mockito.when(mapper.toEntity(postDTO)).thenReturn(r);
        Mockito.when(authentication.getName()).thenReturn("7dce5f33-8375-4514-85c8-9968681c4815");
        Mockito.when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);
        Mockito.when(usuarioRepository.findById(Mockito.any())).thenReturn(Optional.empty());

        Throwable erro = Assertions.catchThrowable(() -> service.save(postDTO));

        Assertions.assertThat(erro).isInstanceOf(UsuarioNaoEncontradoException.class).hasMessage("Usuario não encontrado.");

        Mockito.verify(registroRepository, Mockito.never()).save(Mockito.any());
    }

    @Test
    void deveLancarExcecaoQuandoCategoriaNaoEncontradaAoSalvarRegistro(){
        Usuario u = criarUsuario();
        Categoria c = criarCategoria(u);
        UUID cId = c.getId();
        RegistroPostDTO postDTO = new RegistroPostDTO(LocalDate.now(), 10, "", "", "", Set.of(cId));
        Registro r = criarRegistro(c, u);

        Mockito.when(mapper.toEntity(postDTO)).thenReturn(r);
        Mockito.when(authentication.getName()).thenReturn("7dce5f33-8375-4514-85c8-9968681c4815");
        Mockito.when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);
        Mockito.when(usuarioRepository.findById(Mockito.any())).thenReturn(Optional.of(u));
        Mockito.when(categoriaRepository.findAllByIdInAndUsuarioId(Mockito.eq(Set.of(cId)), Mockito.any())).thenReturn(Set.of());

        Throwable erro = Assertions.catchThrowable(() -> service.save(postDTO));

        Assertions.assertThat(erro).isInstanceOf(CategoriaNaoEncontradaException.class).hasMessage("Uma ou mais categorias não existem!");

        Mockito.verify(registroRepository, Mockito.never()).save(Mockito.any());
    }

    @Test
    void deveAcharRegistroPeloId(){
        Usuario u = criarUsuario();
        Categoria c = criarCategoria(u);
        Registro r = criarRegistro(c, u);
        CategoriaResponseDTO cResponseDTO = new CategoriaResponseDTO(c.getId(), c.getNomeCategoria());

        Mockito.when(registroRepository.buscarPorIdComCategorias(Mockito.any())).thenReturn(Optional.of(r));
        Mockito.when(authentication.getName()).thenReturn("7dce5f33-8375-4514-85c8-9968681c4815");
        Mockito.when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);
        Mockito.when(mapper.toDTO(r))
                .thenReturn(new RegistroResponseDTO(r.getId(),
                        r.getData(), 10, "", "", "", Set.of(cResponseDTO)));

        RegistroResponseDTO rEncontrado = service.findById(r.getId());

        Assertions.assertThat(rEncontrado).isNotNull();
        Assertions.assertThat(rEncontrado.id()).isEqualTo(r.getId());
        Assertions.assertThat(rEncontrado.data()).isEqualTo(r.getData());
        Assertions.assertThat(rEncontrado.categorias()).extracting(CategoriaResponseDTO::nomeCategoria).containsExactlyInAnyOrder(c.getNomeCategoria());
    }

    @Test
    void deveLancarExcecaoQuandoRegistroNaoEncontrado(){
        UUID uuid = UUID.randomUUID();

        Mockito.when(registroRepository.buscarPorIdComCategorias(uuid)).thenReturn(Optional.empty());

        Throwable erro = Assertions.catchThrowable(() -> service.findById(uuid));

        Assertions.assertThat(erro).isInstanceOf(RegistroNaoEncontradoException.class).hasMessage("Registro não encontrado!");

        Mockito.verify(registroRepository, Mockito.times(1)).buscarPorIdComCategorias(uuid);
        Mockito.verifyNoMoreInteractions(registroRepository);
    }

    @Test
    void deveLancarExcecaoQuandoIdUsuarioNaoCorrespondeComIdAuthentication(){
        Usuario u = criarUsuario();
        Categoria c = criarCategoria(u);
        Registro r = criarRegistro(c, u);

        Mockito.when(registroRepository.buscarPorIdComCategorias(r.getId())).thenReturn(Optional.of(r));
        Mockito.when(authentication.getName()).thenReturn(String.valueOf(UUID.randomUUID()));
        Mockito.when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);

        Throwable erro = Assertions.catchThrowable(() -> service.findById(r.getId()));

        Assertions.assertThat(erro).isInstanceOf(RegistroNaoEncontradoException.class).hasMessage("Registro não encontrado.");

        Mockito.verify(registroRepository, Mockito.times(1)).buscarPorIdComCategorias(r.getId());
        Mockito.verifyNoMoreInteractions(registroRepository);
    }

    @Test
    void deveDeletarRegistro(){
        Usuario u = criarUsuario();
        Categoria c = criarCategoria(u);
        Registro r = criarRegistro(c, u);

        Mockito.when(authentication.getName()).thenReturn("7dce5f33-8375-4514-85c8-9968681c4815");
        Mockito.when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);
        Mockito.when(registroRepository.findById(Mockito.any())).thenReturn(Optional.of(r));

        service.delete(r.getId());

        Mockito.verify(registroRepository, Mockito.times(1)).delete(Mockito.any(Registro.class));
    }

    @Test
    void deveLancarExcecaoQuandoRegistroNaoEncontradoParaDeletar(){
        UUID uuid = UUID.randomUUID();

        Mockito.when(authentication.getName()).thenReturn("7dce5f33-8375-4514-85c8-9968681c4815");
        Mockito.when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);
        Mockito.when(registroRepository.findById(uuid)).thenReturn(Optional.empty());

        Throwable erro = Assertions.catchThrowable(() -> service.delete(uuid));

        Assertions.assertThat(erro).isInstanceOf(RegistroNaoEncontradoException.class).hasMessage("Registro não encontrado.");

        Mockito.verify(registroRepository, Mockito.times(1)).findById(uuid);
        Mockito.verifyNoMoreInteractions(registroRepository);
    }

    @Test
    void deveLancarExcecaoQuandoIdUsuarioNaoCorrespondeComIdAuthenticationParaDeletar(){
        Usuario u = criarUsuario();
        Categoria c = criarCategoria(u);
        Registro r = criarRegistro(c, u);

        Mockito.when(authentication.getName()).thenReturn(String.valueOf(UUID.randomUUID()));
        Mockito.when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);
        Mockito.when(registroRepository.findById(r.getId())).thenReturn(Optional.of(r));

        Throwable erro = Assertions.catchThrowable(() -> service.delete(r.getId()));

        Assertions.assertThat(erro).isInstanceOf(RegistroNaoEncontradoException.class).hasMessage("Registro não encontrado.");

        Mockito.verify(registroRepository, Mockito.times(1)).findById(r.getId());
        Mockito.verifyNoMoreInteractions(registroRepository);
    }

    @Test
    void deveProcurarRegistroPorCamposVazio(){
        Usuario u = criarUsuario();
        Categoria c = criarCategoria(u);
        Registro r = criarRegistro(c, u);
        Page<Registro> page = new PageImpl<>(List.of(r));

        mockBase(r, page, c);

        Page<RegistroResponseDTO> resultado =
                service.search(null, null, null, null, null, null, 0, 10);

        Assertions.assertThat(resultado).isNotEmpty();
        Assertions.assertThat(resultado.getContent()).hasSize(1);
        Assertions.assertThat(resultado.getContent().get(0).id()).isEqualTo(r.getId());

        Mockito.verify(registroRepository).findAll(Mockito.<Specification<Registro>>any(), Mockito.eq(PageRequest.of(0, 10)));
    }

    @Test
    void deveProcurarRegistroPorCamposAnoMesDia(){
        Usuario u = criarUsuario();
        Categoria c = criarCategoria(u);
        Registro r = criarRegistro(c, u);
        Page<Registro> page = new PageImpl<>(List.of(r));

        mockBase(r, page, c);

        Page<RegistroResponseDTO> resultado =
                service.search(2000, 10, 13, null, null, null, 0, 10);

        Assertions.assertThat(resultado).isNotEmpty();
        Assertions.assertThat(resultado.getContent()).hasSize(1);
        Assertions.assertThat(resultado.getContent().get(0).id()).isEqualTo(r.getId());

        Mockito.verify(registroRepository).findAll(Mockito.<Specification<Registro>>any(), Mockito.eq(PageRequest.of(0, 10)));
    }

    @Test
    void deveProcurarRegistroPorCamposAnoMes(){
        Usuario u = criarUsuario();
        Categoria c = criarCategoria(u);
        Registro r = criarRegistro(c, u);
        Page<Registro> page = new PageImpl<>(List.of(r));

        mockBase(r, page, c);

        Page<RegistroResponseDTO> resultado =
                service.search(2000, 10, null, null, null, null, 0, 10);

        Assertions.assertThat(resultado).isNotEmpty();
        Assertions.assertThat(resultado.getContent()).hasSize(1);
        Assertions.assertThat(resultado.getContent().get(0).id()).isEqualTo(r.getId());

        Mockito.verify(registroRepository).findAll(Mockito.<Specification<Registro>>any(), Mockito.eq(PageRequest.of(0, 10)));
    }

    @Test
    void deveProcurarRegistroPorCampoAno(){
        Usuario u = criarUsuario();
        Categoria c = criarCategoria(u);
        Registro r = criarRegistro(c, u);
        Page<Registro> page = new PageImpl<>(List.of(r));

        mockBase(r, page, c);

        Page<RegistroResponseDTO> resultado =
                service.search(2000, null, null, null, null, null, 0, 10);

        Assertions.assertThat(resultado).isNotEmpty();
        Assertions.assertThat(resultado.getContent()).hasSize(1);
        Assertions.assertThat(resultado.getContent().get(0).id()).isEqualTo(r.getId());

        Mockito.verify(registroRepository).findAll(Mockito.<Specification<Registro>>any(), Mockito.eq(PageRequest.of(0, 10)));
    }

    @Test
    void deveProcurarRegistroPorCampoNomeCategoria(){
        Usuario u = criarUsuario();
        Categoria c = criarCategoria(u);
        Registro r = criarRegistro(c, u);
        Page<Registro> page = new PageImpl<>(List.of(r));

        mockBase(r, page, c);

        Page<RegistroResponseDTO> resultado =
                service.search(null, null, null, "Java", null, null, 0, 10);

        Assertions.assertThat(resultado).isNotEmpty();
        Assertions.assertThat(resultado.getContent()).hasSize(1);
        Assertions.assertThat(resultado.getContent().get(0).id()).isEqualTo(r.getId());

        Mockito.verify(registroRepository).findAll(Mockito.<Specification<Registro>>any(), Mockito.eq(PageRequest.of(0, 10)));
    }

    @Test
    void deveProcurarRegistroPorCamposMinMax(){
        Usuario u = criarUsuario();
        Categoria c = criarCategoria(u);
        Registro r = criarRegistro(c, u);
        Page<Registro> page = new PageImpl<>(List.of(r));

        mockBase(r, page, c);

        Page<RegistroResponseDTO> resultado =
                service.search(null, null, null, null, 10, 20, 0, 10);

        Assertions.assertThat(resultado).isNotEmpty();
        Assertions.assertThat(resultado.getContent()).hasSize(1);
        Assertions.assertThat(resultado.getContent().get(0).id()).isEqualTo(r.getId());

        Mockito.verify(registroRepository).findAll(Mockito.<Specification<Registro>>any(), Mockito.eq(PageRequest.of(0, 10)));
    }

    @Test
    void deveAtualizarTodosOsCamposDoRegistro(){
        Usuario u = criarUsuario();
        Categoria c = criarCategoria(u);
        Registro r = criarRegistro(c, u);
        r.setId(UUID.fromString("7dce5f33-8375-4514-85c8-9968681c4815"));
        CategoriaResponseDTO cResponseDTO = new CategoriaResponseDTO(c.getId(), c.getNomeCategoria());

        Mockito.when(authentication.getName()).thenReturn("7dce5f33-8375-4514-85c8-9968681c4815");
        Mockito.when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);
        Mockito.when(registroRepository.findById(r.getId())).thenReturn(Optional.of(r));
        Mockito.when(categoriaRepository.findAllByIdInAndUsuarioId(Set.of(c.getId()), r.getUsuario().getId())).thenReturn(Set.of(c));
        Mockito.when(registroRepository.save(Mockito.any(Registro.class))).thenReturn(r);
        Mockito.when(mapper.toDTO(r)).thenReturn(new RegistroResponseDTO(r.getId(),
                LocalDate.of(2025, 10, 29), 50, "Texto", "Texto2", "Texto3", Set.of(cResponseDTO)));

        RegistroResponseDTO rAtualizado = service.update(String.valueOf(UUID.fromString("7dce5f33-8375-4514-85c8-9968681c4815")),
                new RegistroPatchDTO(r.getData(), 50, "Texto", "Texto2", "Texto3", Set.of(cResponseDTO.id())));

        Assertions.assertThat(rAtualizado).isNotNull();
        Assertions.assertThat(rAtualizado.id()).isEqualTo(r.getId());
        Assertions.assertThat(rAtualizado.data()).isEqualTo(LocalDate.of(2025, 10, 29));
        Assertions.assertThat(rAtualizado.horasEstudadas()).isEqualTo(50);
        Assertions.assertThat(rAtualizado.anotacao()).isEqualTo("Texto");
        Assertions.assertThat(rAtualizado.resumo()).isEqualTo("Texto2");
        Assertions.assertThat(rAtualizado.planejamento()).isEqualTo("Texto3");

        Mockito.verify(registroRepository).save(Mockito.any(Registro.class));
    }

    @Test
    void deveAtualizarTodosOsCamposDoRegistroNulos(){
        Usuario u = criarUsuario();
        Categoria c = criarCategoria(u);
        Registro r = criarRegistro(c, u);
        r.setAnotacao("Texto");
        r.setResumo("Texto2");
        r.setPlanejamento("Texto3");
        r.setId(UUID.fromString("7dce5f33-8375-4514-85c8-9968681c4815"));
        CategoriaResponseDTO cResponseDTO = new CategoriaResponseDTO(c.getId(), c.getNomeCategoria());
        LocalDate data = r.getData();
        Integer horasEstudadas = r.getHorasEstudadas();

        Mockito.when(authentication.getName()).thenReturn("7dce5f33-8375-4514-85c8-9968681c4815");
        Mockito.when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);
        Mockito.when(registroRepository.findById(r.getId())).thenReturn(Optional.of(r));
        Mockito.when(registroRepository.save(Mockito.any(Registro.class))).thenReturn(r);
        Mockito.when(mapper.toDTO(r)).thenReturn(new RegistroResponseDTO(r.getId(),
                r.getData(), r.getHorasEstudadas(), r.getAnotacao(), r.getResumo(), r.getPlanejamento(), Set.of(cResponseDTO)));

        RegistroResponseDTO rAtualizado = service.update(String.valueOf(UUID.fromString("7dce5f33-8375-4514-85c8-9968681c4815")),
                new RegistroPatchDTO(null, null, null, null, null, null));

        Assertions.assertThat(r.getData()).isEqualTo(data);
        Assertions.assertThat(r.getHorasEstudadas()).isEqualTo(horasEstudadas);
        Assertions.assertThat(r.getAnotacao()).isEqualTo("Texto");
        Assertions.assertThat(r.getResumo()).isEqualTo("Texto2");
        Assertions.assertThat(r.getPlanejamento()).isEqualTo("Texto3");
        Assertions.assertThat(rAtualizado.id()).isEqualTo(r.getId());
        Assertions.assertThat(rAtualizado.data()).isEqualTo(data);
        Assertions.assertThat(rAtualizado.horasEstudadas()).isEqualTo(horasEstudadas);
        Assertions.assertThat(rAtualizado.anotacao()).isEqualTo("Texto");
        Assertions.assertThat(rAtualizado.resumo()).isEqualTo("Texto2");
        Assertions.assertThat(rAtualizado.planejamento()).isEqualTo("Texto3");

        Mockito.verify(registroRepository).save(Mockito.any(Registro.class));
    }

    @Test
    void deveLancarExcecaoQuandoNemTodasCategoriasForemEncontradas(){
        Usuario u = criarUsuario();
        Categoria c = criarCategoria(u);
        Categoria c2 = criarCategoria(u);
        c2.setId(UUID.randomUUID());
        Registro r = criarRegistro(c, u);
        r.setId(UUID.fromString("7dce5f33-8375-4514-85c8-9968681c4815"));
        CategoriaResponseDTO cResponseDTO = new CategoriaResponseDTO(c.getId(), c.getNomeCategoria());
        CategoriaResponseDTO cResponseDTO2 = new CategoriaResponseDTO(c2.getId(), c2.getNomeCategoria());

        Mockito.when(authentication.getName()).thenReturn("7dce5f33-8375-4514-85c8-9968681c4815");
        Mockito.when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);
        Mockito.when(registroRepository.findById(r.getId())).thenReturn(Optional.of(r));
        Mockito.when(categoriaRepository.findAllByIdInAndUsuarioId(Mockito.anySet(), Mockito.any())).thenReturn(Set.of(c));

        Throwable erro = Assertions.catchThrowable(() -> service.update(String.valueOf(UUID.fromString("7dce5f33-8375-4514-85c8-9968681c4815")),
                new RegistroPatchDTO(null, null, null, null, null, Set.of(cResponseDTO.id(), cResponseDTO2.id()))));

        Assertions.assertThat(erro).isInstanceOf(CategoriaNaoEncontradaException.class).hasMessage("Categoria nao encontrada.");

        Mockito.verify(registroRepository, Mockito.never()).save(Mockito.any());
    }
}
