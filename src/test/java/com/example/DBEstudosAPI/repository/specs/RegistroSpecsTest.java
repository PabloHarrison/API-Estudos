package com.example.DBEstudosAPI.repository.specs;

import com.example.DBEstudosAPI.entities.Categoria;
import com.example.DBEstudosAPI.entities.Registro;
import com.example.DBEstudosAPI.entities.Usuario;
import com.example.DBEstudosAPI.repository.CategoriaRepository;
import com.example.DBEstudosAPI.repository.RegistroRepository;
import com.example.DBEstudosAPI.repository.UsuarioRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;

@DataJpaTest
@ActiveProfiles("test")
public class RegistroSpecsTest {

    @Autowired
    UsuarioRepository usuarioRepository;
    @Autowired
    CategoriaRepository categoriaRepository;
    @Autowired
    RegistroRepository registroRepository;

    private Usuario criarUsuario(){
        return new Usuario();
    }
    private Categoria criarCategoria(Usuario u){
        Categoria c = new Categoria();
        c.setUsuario(u);
        c.setNomeCategoria("Java");
        return c;
    }
    private Registro criarRegistro(Categoria c, Usuario u){
        Registro r = new Registro();
        r.setCategorias(Set.of(c));
        r.setUsuario(u);
        r.setData(LocalDate.now());
        r.setHorasEstudadas(10);
        return r;
    }

    @Test
    void deveFiltarPorAno(){
        Usuario u = criarUsuario();
        usuarioRepository.save(u);
        Categoria c = criarCategoria(u);
        categoriaRepository.save(c);
        Registro r = criarRegistro(c, u);
        r.setData(LocalDate.of(2025, 3, 30));
        Registro r2 = criarRegistro(c, u);
        r2.setData(LocalDate.of(2024, 3, 30));
        registroRepository.saveAll(Set.of(r, r2));

        Specification<Registro> registroSpecs = RegistroSpecs.dataAnoEquals(r.getData().getYear());

        List<Registro> registros = registroRepository.findAll(registroSpecs);

        Assertions.assertThat(registroSpecs).isNotNull();
        Assertions.assertThat(registros).hasSize(1);
        Assertions.assertThat(registros).extracting(Registro::getData).contains(r.getData());
    }

    @Test
    void deveFiltrarPorAnoEMes(){
        Usuario u = criarUsuario();
        usuarioRepository.save(u);
        Categoria c = criarCategoria(u);
        categoriaRepository.save(c);
        Registro r = criarRegistro(c, u);
        r.setData(LocalDate.of(2025, 3, 30));
        Registro r2 = criarRegistro(c, u);
        r2.setData(LocalDate.of(2025, 5, 30));
        registroRepository.saveAll(Set.of(r, r2));

        Specification<Registro> registroSpecs = RegistroSpecs.dataAnoMesEquals(r.getData().getYear(), r.getData().getMonthValue());

        List<Registro> registros = registroRepository.findAll(registroSpecs);

        Assertions.assertThat(registroSpecs).isNotNull();
        Assertions.assertThat(registros).hasSize(1);
        Assertions.assertThat(registros).extracting(Registro::getData).contains(r.getData());
    }

    @Test
    void deveFiltrarPorAnoNuloEMes(){
        Usuario u = criarUsuario();
        usuarioRepository.save(u);
        Categoria c = criarCategoria(u);
        categoriaRepository.save(c);
        Registro r = criarRegistro(c, u);
        r.setData(LocalDate.of(2025, 3, 30));
        Registro r2 = criarRegistro(c, u);
        r2.setData(LocalDate.of(2025, 3, 24));
        registroRepository.saveAll(Set.of(r, r2));

        Specification<Registro> registroSpecs = RegistroSpecs.dataAnoMesEquals(null, r.getData().getMonthValue());

        List<Registro> registros = registroRepository.findAll(registroSpecs);

        Assertions.assertThat(registroSpecs).isNotNull();
        Assertions.assertThat(registros).hasSize(2);
        Assertions.assertThat(registros).extracting(Registro::getData).containsExactlyInAnyOrder(r.getData(), r2.getData());
    }

    @Test
    void deveFiltrarPorAnoEMesNulo(){
        Usuario u = criarUsuario();
        usuarioRepository.save(u);
        Categoria c = criarCategoria(u);
        categoriaRepository.save(c);
        Registro r = criarRegistro(c, u);
        r.setData(LocalDate.of(2025, 3, 30));
        Registro r2 = criarRegistro(c, u);
        r2.setData(LocalDate.of(2025, 5, 24));
        registroRepository.saveAll(Set.of(r, r2));

        Specification<Registro> registroSpecs = RegistroSpecs.dataAnoMesEquals(r.getData().getYear(),null);

        List<Registro> registros = registroRepository.findAll(registroSpecs);

        Assertions.assertThat(registroSpecs).isNotNull();
        Assertions.assertThat(registros).hasSize(2);
        Assertions.assertThat(registros).extracting(Registro::getData).containsExactlyInAnyOrder(r.getData(), r2.getData());
    }

    @Test
    void deveFiltrarPorAnoMesEDia(){
        Usuario u = criarUsuario();
        usuarioRepository.save(u);
        Categoria c = criarCategoria(u);
        categoriaRepository.save(c);
        Registro r = criarRegistro(c, u);
        r.setData(LocalDate.of(2025, 3, 30));
        Registro r2 = criarRegistro(c, u);
        r2.setData(LocalDate.of(2025, 3, 13));
        registroRepository.saveAll(Set.of(r, r2));

        Specification<Registro> registroSpecs = RegistroSpecs.dataAnoMesDiaEquals(r.getData().getYear(), r.getData().getMonthValue(), r.getData().getDayOfMonth());

        List<Registro> registros = registroRepository.findAll(registroSpecs);

        Assertions.assertThat(registroSpecs).isNotNull();
        Assertions.assertThat(registros).hasSize(1);
        Assertions.assertThat(registros).extracting(Registro::getData).contains(r.getData());
    }

    @Test
    void deveFiltrarPorAnoNuloMesEDia(){
        Usuario u = criarUsuario();
        usuarioRepository.save(u);
        Categoria c = criarCategoria(u);
        categoriaRepository.save(c);
        Registro r = criarRegistro(c, u);
        r.setData(LocalDate.of(2025, 3, 30));
        Registro r2 = criarRegistro(c, u);
        r2.setData(LocalDate.of(2025, 3, 13));
        registroRepository.saveAll(Set.of(r, r2));

        Specification<Registro> registroSpecs = RegistroSpecs.dataAnoMesDiaEquals(null, r.getData().getMonthValue(), r.getData().getDayOfMonth());

        List<Registro> registros = registroRepository.findAll(registroSpecs);

        Assertions.assertThat(registroSpecs).isNotNull();
        Assertions.assertThat(registros).hasSize(2);
        Assertions.assertThat(registros).extracting(Registro::getData).containsExactlyInAnyOrder(r.getData(), r2.getData());
    }

    @Test
    void deveFiltrarPorAnoMesNullEDia(){
        Usuario u = criarUsuario();
        usuarioRepository.save(u);
        Categoria c = criarCategoria(u);
        categoriaRepository.save(c);
        Registro r = criarRegistro(c, u);
        r.setData(LocalDate.of(2025, 3, 30));
        Registro r2 = criarRegistro(c, u);
        r2.setData(LocalDate.of(2025, 3, 13));
        registroRepository.saveAll(Set.of(r, r2));

        Specification<Registro> registroSpecs = RegistroSpecs.dataAnoMesDiaEquals(r.getData().getYear(), null, r.getData().getDayOfMonth());

        List<Registro> registros = registroRepository.findAll(registroSpecs);

        Assertions.assertThat(registroSpecs).isNotNull();
        Assertions.assertThat(registros).hasSize(2);
        Assertions.assertThat(registros).extracting(Registro::getData).containsExactlyInAnyOrder(r.getData(), r2.getData());
    }

    @Test
    void deveFiltrarPorAnoMesEDiaNull(){
        Usuario u = criarUsuario();
        usuarioRepository.save(u);
        Categoria c = criarCategoria(u);
        categoriaRepository.save(c);
        Registro r = criarRegistro(c, u);
        r.setData(LocalDate.of(2025, 3, 30));
        Registro r2 = criarRegistro(c, u);
        r2.setData(LocalDate.of(2025, 3, 13));
        registroRepository.saveAll(Set.of(r, r2));

        Specification<Registro> registroSpecs = RegistroSpecs.dataAnoMesDiaEquals(r.getData().getYear(), r.getData().getMonthValue(), null);

        List<Registro> registros = registroRepository.findAll(registroSpecs);

        Assertions.assertThat(registroSpecs).isNotNull();
        Assertions.assertThat(registros).hasSize(2);
        Assertions.assertThat(registros).extracting(Registro::getData).containsExactlyInAnyOrder(r.getData(), r2.getData());
    }

    @Test
    void deveFiltarPorTempoMinEMax(){
        Usuario u = criarUsuario();
        usuarioRepository.save(u);
        Categoria c = criarCategoria(u);
        categoriaRepository.save(c);
        Registro r = criarRegistro(c, u);
        r.setHorasEstudadas(5);
        registroRepository.save(r);

        Specification<Registro> registroSpecs = RegistroSpecs.tempoBetween(1, 5);

        List<Registro> registros = registroRepository.findAll(registroSpecs);

        Assertions.assertThat(registroSpecs).isNotNull();
        Assertions.assertThat(registros).hasSize(1);
        Assertions.assertThat(registros).extracting(Registro::getHorasEstudadas).contains(5);
    }

    @Test
    void deveFiltarPorTempoMinEMaxNulos(){
        Usuario u = criarUsuario();
        usuarioRepository.save(u);
        Categoria c = criarCategoria(u);
        categoriaRepository.save(c);
        Registro r = criarRegistro(c, u);
        r.setHorasEstudadas(5);
        Registro r2 = criarRegistro(c, u);
        r2.setHorasEstudadas(7);
        registroRepository.saveAll(Set.of(r, r2));

        Specification<Registro> registroSpecs = RegistroSpecs.tempoBetween(null, null);

        List<Registro> registros = registroRepository.findAll(registroSpecs);

        Assertions.assertThat(registroSpecs).isNotNull();
        Assertions.assertThat(registros).hasSize(2);
        Assertions.assertThat(registros).extracting(Registro::getHorasEstudadas).containsExactlyInAnyOrder(5, 7);
    }

    @Test
    void deveFiltrarPorTempoMaxNulo(){
        Usuario u = criarUsuario();
        usuarioRepository.save(u);
        Categoria c = criarCategoria(u);
        categoriaRepository.save(c);
        Registro r = criarRegistro(c, u);
        r.setHorasEstudadas(5);
        Registro r2 = criarRegistro(c, u);
        r2.setHorasEstudadas(7);
        registroRepository.saveAll(Set.of(r, r2));

        Specification<Registro> registroSpecs = RegistroSpecs.tempoBetween(6, null);

        List<Registro> registros = registroRepository.findAll(registroSpecs);

        Assertions.assertThat(registroSpecs).isNotNull();
        Assertions.assertThat(registros).hasSize(1);
        Assertions.assertThat(registros).extracting(Registro::getHorasEstudadas).contains(7);
    }

    @Test
    void deveFiltrarPorTempoMinNulo(){
        Usuario u = criarUsuario();
        usuarioRepository.save(u);
        Categoria c = criarCategoria(u);
        categoriaRepository.save(c);
        Registro r = criarRegistro(c, u);
        r.setHorasEstudadas(5);
        Registro r2 = criarRegistro(c, u);
        r2.setHorasEstudadas(7);
        registroRepository.saveAll(Set.of(r, r2));

        Specification<Registro> registroSpecs = RegistroSpecs.tempoBetween(null, 6);

        List<Registro> registros = registroRepository.findAll(registroSpecs);

        Assertions.assertThat(registroSpecs).isNotNull();
        Assertions.assertThat(registros).hasSize(1);
        Assertions.assertThat(registros).extracting(Registro::getHorasEstudadas).contains(5);
    }

    @Test
    void deveFiltrarPorNomeCategoria(){
        Usuario u = criarUsuario();
        usuarioRepository.save(u);
        Categoria c = criarCategoria(u);
        categoriaRepository.save(c);
        Registro r = criarRegistro(c, u);
        registroRepository.save(r);

        Specification<Registro> registroSpecs = RegistroSpecs.nomeCategoriaLike("Java");

        List<Registro> registros = registroRepository.findAll(registroSpecs);

        Assertions.assertThat(registroSpecs).isNotNull();
        Assertions.assertThat(registros).hasSize(1);
        Assertions.assertThat(registros).flatExtracting(Registro::getCategorias).extracting(Categoria::getNomeCategoria).contains("Java");
    }

    @Test
    void deveFiltrarPorNomeCategoriaVazio(){
        Usuario u = criarUsuario();
        usuarioRepository.save(u);
        Categoria c = criarCategoria(u);
        Categoria c2 = criarCategoria(u);
        c2.setNomeCategoria("Spring");
        categoriaRepository.saveAll(List.of(c, c2));
        Registro r = criarRegistro(c, u);
        r.setCategorias(Set.of(c, c2));
        registroRepository.save(r);

        Specification<Registro> registroSpecs = RegistroSpecs.nomeCategoriaLike("");

        List<Registro> registros = registroRepository.findAll(registroSpecs);

        Assertions.assertThat(registroSpecs).isNotNull();
        Assertions.assertThat(registros).hasSize(1);
        Assertions.assertThat(registros).flatExtracting(Registro::getCategorias).extracting(Categoria::getNomeCategoria).containsExactlyInAnyOrder("Java", "Spring");
    }
}
