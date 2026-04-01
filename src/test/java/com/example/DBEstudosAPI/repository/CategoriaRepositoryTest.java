package com.example.DBEstudosAPI.repository;

import com.example.DBEstudosAPI.entities.Categoria;
import com.example.DBEstudosAPI.entities.Usuario;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@DataJpaTest
@ActiveProfiles("test")
public class CategoriaRepositoryTest {

    @Autowired
    CategoriaRepository repository;
    @Autowired
    UsuarioRepository usuarioRepository;

    @Test
    void deveBuscarCategoriasPorNomeEUsuario(){
        Usuario u = new Usuario();
        Categoria c1 = new Categoria();
        c1.setNomeCategoria("Java");
        c1.setUsuario(u);
        Categoria c2 = new Categoria();
        c2.setNomeCategoria("JavaScript");
        c2.setUsuario(u);
        Categoria c3 = new Categoria();
        c3.setUsuario(u);
        c3.setNomeCategoria("Spring");

        usuarioRepository.save(u);
        repository.saveAll(List.of(c1, c2, c3));

        Set<Categoria> categorias = repository.findAllByNomeCategoriaContainingIgnoreCaseAndUsuarioId("jav", u.getId());

        Assertions.assertThat(categorias).hasSize(2);
        Assertions.assertThat(categorias).extracting(Categoria::getNomeCategoria).containsExactlyInAnyOrder("Java", "JavaScript");
    }

    @Test
    void deveBuscarCategoriasVaziasPorNomeEUsuario(){
        Usuario u = new Usuario();
        Categoria c1 = new Categoria();
        c1.setNomeCategoria("Java");
        c1.setUsuario(u);
        Categoria c2 = new Categoria();
        c2.setNomeCategoria("JavaScript");
        c2.setUsuario(u);
        Categoria c3 = new Categoria();
        c3.setUsuario(u);
        c3.setNomeCategoria("Spring");

        usuarioRepository.save(u);
        repository.saveAll(List.of(c1, c2, c3));

        Set<Categoria> categorias = repository.findAllByNomeCategoriaContainingIgnoreCaseAndUsuarioId("python", u.getId());

        Assertions.assertThat(categorias).isEmpty();
        Assertions.assertThat(categorias).isNotNull();
    }

    @Test
    void deveBuscarCategoriasPorIdsEUsuarioId(){
        Usuario u = new Usuario();
        Categoria c1 = new Categoria();
        c1.setNomeCategoria("Java");
        c1.setUsuario(u);
        Categoria c2 = new Categoria();
        c2.setNomeCategoria("Spring");
        c2.setUsuario(u);

        usuarioRepository.save(u);
        List<Categoria> categoriasSalvas = repository.saveAll(List.of(c1, c2));

        Set<UUID> ids = categoriasSalvas.stream().map(Categoria::getId).collect(Collectors.toSet());

        Set<Categoria> categorias = repository.findAllByIdInAndUsuarioId(ids, u.getId());

        Assertions.assertThat(categorias).hasSize(2);
        Assertions.assertThat(categorias).extracting(Categoria::getNomeCategoria).containsExactlyInAnyOrder("Java", "Spring");
        Assertions.assertThat(ids).doesNotContainNull();
    }

    @Test
    void deveBuscarCategoriasVaziasPorIdsEUsuarioId(){
        Usuario u = new Usuario();
        Categoria c1 = new Categoria();
        c1.setNomeCategoria("Java");
        c1.setUsuario(u);
        Categoria c2 = new Categoria();
        c2.setNomeCategoria("Spring");
        c2.setUsuario(u);

        usuarioRepository.save(u);
        repository.saveAll(List.of(c1, c2));

        Set<Categoria> categorias = repository.findAllByIdInAndUsuarioId(Set.of(UUID.randomUUID()), u.getId());

        Assertions.assertThat(categorias).isEmpty();
        Assertions.assertThat(categorias).isNotNull();
    }
}
