package com.example.DBEstudosAPI.service;

import com.example.DBEstudosAPI.dto.CategoriaPatchDTO;
import com.example.DBEstudosAPI.dto.CategoriaPostDTO;
import com.example.DBEstudosAPI.dto.CategoriaResponseDTO;
import com.example.DBEstudosAPI.entities.Categoria;
import com.example.DBEstudosAPI.entities.Usuario;
import com.example.DBEstudosAPI.exceptions.CategoriaEmUsoException;
import com.example.DBEstudosAPI.exceptions.CategoriaNaoEncontradaException;
import com.example.DBEstudosAPI.exceptions.CategoriaNaoPermitidaException;
import com.example.DBEstudosAPI.exceptions.UsuarioNaoEncontradoException;
import com.example.DBEstudosAPI.mappers.CategoriaMapper;
import com.example.DBEstudosAPI.repository.CategoriaRepository;
import com.example.DBEstudosAPI.repository.RegistroRepository;
import com.example.DBEstudosAPI.repository.UsuarioRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

@ExtendWith(MockitoExtension.class)
public class CategoriaServiceTest {

    @InjectMocks
    CategoriaService service;

    @Mock
    CategoriaRepository categoriaRepository;
    @Mock
    UsuarioRepository usuarioRepository;
    @Mock
    RegistroRepository registroRepository;
    @Mock
    CategoriaMapper mapper;
    @Mock
    SecurityContext securityContext;
    @Mock
    Authentication authentication;

    @AfterEach
    void limparContext(){
        SecurityContextHolder.clearContext();
    }

    private Usuario criarUsuario() {
        Usuario u = new Usuario();
        u.setId(UUID.fromString("7dce5f33-8375-4514-85c8-9968681c4815"));
        return u;
    }
    private Categoria criarCategoria(Usuario u) {
        Categoria c = new Categoria();
        c.setNomeCategoria("Java");
        c.setUsuario(u);
        return c;
    }

    @Test
    void deveSalvarCategoria(){
        CategoriaPostDTO cDto = new CategoriaPostDTO("Java");
        Usuario u = criarUsuario();
        Categoria c = criarCategoria(u);

        Mockito.when(mapper.toEntity(cDto)).thenReturn(c);
        Mockito.when(authentication.getName()).thenReturn("7dce5f33-8375-4514-85c8-9968681c4815");
        Mockito.when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);
        Mockito.when(usuarioRepository.findById(Mockito.any())).thenReturn(Optional.of(u));
        Mockito.when(categoriaRepository.save(Mockito.any())).thenReturn(c);
        Mockito.when(mapper.toDTO(c)).thenReturn(new CategoriaResponseDTO(UUID.fromString("cdc76582-dd3a-4647-a224-2de82b9415ab"), "Java"));

        CategoriaResponseDTO cSalvo = service.save(cDto);

        Assertions.assertThat(cSalvo).isNotNull();
        Assertions.assertThat(cSalvo.nomeCategoria()).isEqualTo("Java");

        Mockito.verify(categoriaRepository).save(c);
    }

    @Test
    void deveLancarExcecaoQuandoUsuarioNaoEncontrado(){
        CategoriaPostDTO cDto = new CategoriaPostDTO("Java");
        Usuario u = criarUsuario();
        Categoria c = criarCategoria(u);

        Mockito.when(mapper.toEntity(cDto)).thenReturn(c);
        Mockito.when(authentication.getName()).thenReturn("7dce5f33-8375-4514-85c8-9968681c4815");
        Mockito.when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);
        Mockito.when(usuarioRepository.findById(Mockito.any())).thenReturn(Optional.empty());

        Throwable erro = Assertions.catchThrowable(() -> service.save(cDto));

        Assertions.assertThat(erro).isInstanceOf(UsuarioNaoEncontradoException.class);

        Mockito.verify(categoriaRepository, Mockito.never()).save(Mockito.any());
    }

    //FindEntity

    @Test
    void deveAcharEntidadePeloId(){
        Usuario u = criarUsuario();
        Categoria c = criarCategoria(u);
        UUID usuarioId = UUID.fromString("7dce5f33-8375-4514-85c8-9968681c4815");

        Mockito.when(authentication.getName()).thenReturn("7dce5f33-8375-4514-85c8-9968681c4815");
        Mockito.when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);
        Mockito.when(categoriaRepository.findById(Mockito.any())).thenReturn(Optional.of(c));

        Categoria cEncontrado = service.findEntityById(c.getId());

        Assertions.assertThat(cEncontrado).isNotNull();
        Assertions.assertThat(cEncontrado.getNomeCategoria()).isEqualTo("Java");
        Assertions.assertThat(cEncontrado.getUsuario().getId()).isEqualTo(usuarioId);
    }

    @Test
    void deveLancarExcecaoQuandoCategoriaNaoEncontrada(){
        UUID id = UUID.randomUUID();
        Mockito.when(authentication.getName()).thenReturn("7dce5f33-8375-4514-85c8-9968681c4815");
        Mockito.when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);
        Mockito.when(categoriaRepository.findById(Mockito.any())).thenReturn(Optional.empty());

        Throwable erro = Assertions.catchThrowable(() -> service.findEntityById(id));

        Assertions.assertThat(erro).isInstanceOf(CategoriaNaoEncontradaException.class);

        Mockito.verify(categoriaRepository, Mockito.times(1)).findById(Mockito.any());
    }

    @Test
    void deveLancarExcecaoQuandoCategoriaNaoPertenceAoUsuario(){
        Usuario u = criarUsuario();
        Categoria c = criarCategoria(u);
        UUID categoriaId = UUID.randomUUID();
        UUID usuarioIdToken = UUID.randomUUID();

        Mockito.when(authentication.getName()).thenReturn(String.valueOf(usuarioIdToken));
        Mockito.when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);
        Mockito.when(categoriaRepository.findById(Mockito.any())).thenReturn(Optional.of(c));

        Throwable erro = Assertions.catchThrowable(() -> service.findEntityById(categoriaId));

        Assertions.assertThat(erro).isInstanceOf(CategoriaNaoEncontradaException.class);
        Mockito.verify(categoriaRepository, Mockito.times(1)).findById(Mockito.any());
    }

    @Test
    void deveAcharCategoriaPeloId(){
        Usuario u = criarUsuario();
        Categoria c = criarCategoria(u);

        Mockito.when(authentication.getName()).thenReturn("7dce5f33-8375-4514-85c8-9968681c4815");
        Mockito.when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);
        Mockito.when(categoriaRepository.findById(Mockito.any())).thenReturn(Optional.of(c));
        Mockito.when(mapper.toDTO(c)).thenReturn(new CategoriaResponseDTO(UUID.fromString("cdc76582-dd3a-4647-a224-2de82b9415ab"), "Java"));

        CategoriaResponseDTO cEncontrada = service.findById(c.getId());

        Assertions.assertThat(cEncontrada).isNotNull();
        Assertions.assertThat(cEncontrada.nomeCategoria()).isEqualTo("Java");
        Assertions.assertThat(cEncontrada.id()).isEqualTo(UUID.fromString("cdc76582-dd3a-4647-a224-2de82b9415ab"));
    }

    @Test
    void deveAcharCategoriaPorNome(){
        Usuario u = criarUsuario();
        Categoria c1 = criarCategoria(u);
        Categoria c2 = criarCategoria(u);
        c1.setId(UUID.randomUUID());
        c2.setId(UUID.randomUUID());
        Set<Categoria> cSet = new HashSet<>();
        cSet.add(c1);
        cSet.add(c2);

        Mockito.when(authentication.getName()).thenReturn("7dce5f33-8375-4514-85c8-9968681c4815");
        Mockito.when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);
        Mockito.when(mapper.toDTO(c1)).thenReturn(new CategoriaResponseDTO(UUID.fromString("7cd728b6-77c8-4c7b-866e-5cc7ae5303af"), "Java"));
        Mockito.when(mapper.toDTO(c2)).thenReturn(new CategoriaResponseDTO(UUID.fromString("cdc76582-dd3a-4647-a224-2de82b9415ab"), "Java"));
        Mockito.when(categoriaRepository.findAllByNomeCategoriaContainingIgnoreCaseAndUsuarioId(Mockito.any(), Mockito.any()))
                .thenReturn(cSet);

        Set<CategoriaResponseDTO> cSetEncontrada = service.search("Java");

        Assertions.assertThat(cSetEncontrada).isNotEmpty();
        Assertions.assertThat(cSetEncontrada).hasSize(2);
    }

    @Test
    void deveLancarExcecaoQuandoNomeCategoriaForNull(){
        Throwable erro = Assertions.catchThrowable(() -> service.search(null));

        Assertions.assertThat(erro).isInstanceOf(CategoriaNaoEncontradaException.class).hasMessage("Parametro inválido!");
    }

    @Test
    void deveDeletarCategoria(){
        Usuario u = criarUsuario();
        Categoria c = criarCategoria(u);
        UUID categoriaId = UUID.randomUUID();
        c.setId(categoriaId);

        Mockito.when(categoriaRepository.findById(Mockito.any())).thenReturn(Optional.of(c));
        Mockito.when(authentication.getName()).thenReturn("7dce5f33-8375-4514-85c8-9968681c4815");
        Mockito.when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);
        Mockito.when(registroRepository.existsByCategoriasContains(c)).thenReturn(false);

        service.delete(categoriaId);

        Mockito.verify(categoriaRepository, Mockito.times(1)).delete(Mockito.any(Categoria.class));
    }

    @Test
    void deveLancarExcecaoSeExistirRegistroVinculadoACategoria(){
        Usuario u = criarUsuario();
        Categoria c = criarCategoria(u);
        UUID categoriaId = UUID.randomUUID();
        c.setId(categoriaId);

        Mockito.when(categoriaRepository.findById(Mockito.any())).thenReturn(Optional.of(c));
        Mockito.when(authentication.getName()).thenReturn("7dce5f33-8375-4514-85c8-9968681c4815");
        Mockito.when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);
        Mockito.when(registroRepository.existsByCategoriasContains(c)).thenReturn(true);

        Throwable erro = Assertions.catchThrowable(() -> service.delete(categoriaId));

        Assertions.assertThat(erro).isInstanceOf(CategoriaEmUsoException.class)
                .hasMessage("Não é possivel deletar uma categoria que esteja sendo usada por um registro.");

        Mockito.verify(categoriaRepository, Mockito.never()).delete(Mockito.any());
    }

    @Test
    void deveAtualizarCategoria(){
        CategoriaPatchDTO cDto = new CategoriaPatchDTO("Spring");
        Usuario u = criarUsuario();
        Categoria c = criarCategoria(u);

        Mockito.when(categoriaRepository.findById(Mockito.any())).thenReturn(Optional.of(c));
        Mockito.when(authentication.getName()).thenReturn("7dce5f33-8375-4514-85c8-9968681c4815");
        Mockito.when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);
        Mockito.when(categoriaRepository.save(Mockito.any())).thenReturn(c);
        Mockito.when(mapper.toDTO(c)).thenReturn(new CategoriaResponseDTO(UUID.fromString("cdc76582-dd3a-4647-a224-2de82b9415ab"), "Spring"));

        CategoriaResponseDTO cAtualizado = service.update(c.getId(), cDto);

        Assertions.assertThat(cAtualizado).isNotNull();
        Assertions.assertThat(cAtualizado.nomeCategoria()).isEqualTo("Spring");
        Assertions.assertThat(c.getNomeCategoria()).isEqualTo("Spring");

        Mockito.verify(categoriaRepository).save(Mockito.any(Categoria.class));
    }

    @Test
    void deveLancarExcecaoSeNomeDaCategoriaForNuloParaAtualizar(){
        CategoriaPatchDTO cdto = new CategoriaPatchDTO(null);
        Usuario u = criarUsuario();
        Categoria c = criarCategoria(u);

        Mockito.when(categoriaRepository.findById(Mockito.any())).thenReturn(Optional.of(c));
        Mockito.when(authentication.getName()).thenReturn("7dce5f33-8375-4514-85c8-9968681c4815");
        Mockito.when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);

        Throwable erro = Assertions.catchThrowable(() -> service.update(c.getId(), cdto));

        Assertions.assertThat(erro).isInstanceOf(CategoriaNaoPermitidaException.class).hasMessage("É obrigatorio nomear a categoria!");
    }

    @Test
    void deveLancarExcecaoSeNomeDaCategoriaForBlankParaAtualizar(){
        CategoriaPatchDTO cdto = new CategoriaPatchDTO(" ");
        Usuario u = criarUsuario();
        Categoria c = criarCategoria(u);

        Mockito.when(categoriaRepository.findById(Mockito.any())).thenReturn(Optional.of(c));
        Mockito.when(authentication.getName()).thenReturn("7dce5f33-8375-4514-85c8-9968681c4815");
        Mockito.when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);

        Throwable erro = Assertions.catchThrowable(() -> service.update(c.getId(), cdto));

        Assertions.assertThat(erro).isInstanceOf(CategoriaNaoPermitidaException.class).hasMessage("É obrigatorio nomear a categoria!");
    }
}
