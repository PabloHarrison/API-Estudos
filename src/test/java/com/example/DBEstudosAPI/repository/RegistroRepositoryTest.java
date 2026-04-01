package com.example.DBEstudosAPI.repository;

import com.example.DBEstudosAPI.entities.Categoria;
import com.example.DBEstudosAPI.entities.Registro;
import com.example.DBEstudosAPI.entities.Usuario;
import com.example.DBEstudosAPI.repository.specs.RegistroSpecs;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

@DataJpaTest
@ActiveProfiles("test")
public class RegistroRepositoryTest {

    @Autowired
    RegistroRepository repository;
    @Autowired
    CategoriaRepository categoriaRepository;
    @Autowired
    UsuarioRepository usuarioRepository;

    @Test
    void deveBuscarRegistroPeloIdComCategorias(){
        Usuario u = new Usuario();
        usuarioRepository.save(u);
        Categoria c = new Categoria();
        c.setUsuario(u);
        categoriaRepository.save(c);
        Registro r = new Registro();
        r.setData(LocalDate.now());
        r.setHorasEstudadas(10);
        r.setUsuario(u);
        r.setCategorias(Set.of(c));
        repository.save(r);

        Optional<Registro> registro = repository.buscarPorIdComCategorias(r.getId());

        Assertions.assertThat(registro).isPresent().get().extracting(Registro::getId).isEqualTo(r.getId());
        Assertions.assertThat(registro).isPresent().get().extracting(Registro::getCategorias).isEqualTo(r.getCategorias());

    }
    @Test
    void deveBuscarRegistroPeloIdComCategoriasRetornandoVazio(){
        Optional<Registro> registro = repository.buscarPorIdComCategorias(UUID.randomUUID());

        Assertions.assertThat(registro).isEmpty();
    }

    @Test
    void deveBuscarUtilizandoSpecification(){
        Usuario u = new Usuario();
        usuarioRepository.save(u);
        Categoria c = new Categoria();
        c.setUsuario(u);
        categoriaRepository.save(c);
        Registro r = new Registro();
        r.setData(LocalDate.now());
        r.setHorasEstudadas(10);
        r.setUsuario(u);
        r.setCategorias(Set.of(c));
        repository.save(r);
        Specification<Registro> registroSpecification = RegistroSpecs.usuarioIdEquals(u.getId());
        Pageable pageable = PageRequest.of(0, 10);

        Page<Registro> registros = repository.findAll(registroSpecification, pageable);

        Assertions.assertThat(registros).isNotEmpty();
        Assertions.assertThat(registros).hasSize(1);
        Assertions.assertThat(registros.getContent()).extracting(Registro::getId).contains(r.getId());
        Assertions.assertThat(registros.getContent().get(0).getCategorias()).isNotEmpty();
    }

    @Test
    void deveBuscarUtilizandoSpecificationRetornandoVazio(){
        Specification<Registro> registroSpecification = RegistroSpecs.usuarioIdEquals(UUID.randomUUID());
        Pageable pageable = PageRequest.of(0, 10);

        Page<Registro> registros = repository.findAll(registroSpecification, pageable);

        Assertions.assertThat(registros).isEmpty();
    }

    @Test
    void deveBuscarMaisDeUmRegistroLimitandoPaginaUtilizandoSpecification(){
        Usuario u = new Usuario();
        usuarioRepository.save(u);
        Categoria c = new Categoria();
        c.setUsuario(u);
        categoriaRepository.save(c);
        Registro r1 = new Registro();
        r1.setData(LocalDate.now());
        r1.setHorasEstudadas(10);
        r1.setUsuario(u);
        r1.setCategorias(Set.of(c));
        Registro r2 = new Registro();
        r2.setData(LocalDate.now());
        r2.setHorasEstudadas(10);
        r2.setUsuario(u);
        r2.setCategorias(Set.of(c));
        repository.saveAll(Set.of(r1, r2));
        Specification<Registro> registroSpecification = RegistroSpecs.usuarioIdEquals(u.getId());
        Pageable pageable = PageRequest.of(0, 1);

        Page<Registro> registros = repository.findAll(registroSpecification, pageable);

        Assertions.assertThat(registros).isNotEmpty();
        Assertions.assertThat(registros).hasSize(1);
        Assertions.assertThat(registros.getTotalElements()).isEqualTo(2);
        Assertions.assertThat(registros.getContent().get(0).getCategorias()).isNotEmpty();
    }

    @Test
    void deveVerificarSeCategoriaEstaLigadaAlgumRegistro(){
        Usuario u = new Usuario();
        usuarioRepository.save(u);
        Categoria c = new Categoria();
        c.setUsuario(u);
        categoriaRepository.save(c);
        Registro r = new Registro();
        r.setData(LocalDate.now());
        r.setHorasEstudadas(10);
        r.setUsuario(u);
        r.setCategorias(Set.of(c));
        repository.save(r);

        boolean resultado = repository.existsByCategoriasContains(c);

        Assertions.assertThat(resultado).isTrue();
    }

    @Test
    void deveVerificarSeCategoriaEstaLigadaAlgumRegistroRetornandoFalse(){
        Usuario u = new Usuario();
        usuarioRepository.save(u);
        Categoria c1 = new Categoria();
        c1.setUsuario(u);
        Categoria c2 = new Categoria();
        c2.setUsuario(u);
        categoriaRepository.saveAll(List.of(c1, c2));
        Registro r = new Registro();
        r.setData(LocalDate.now());
        r.setHorasEstudadas(10);
        r.setUsuario(u);
        r.setCategorias(Set.of(c1));
        repository.save(r);

        boolean resultado = repository.existsByCategoriasContains(c2);

        Assertions.assertThat(resultado).isFalse();
    }
}
