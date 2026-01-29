package com.example.DBEstudosAPI.repository;

import com.example.DBEstudosAPI.entities.Categoria;
import com.example.DBEstudosAPI.entities.Registro;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;
import java.util.UUID;

public interface RegistroRepository extends JpaRepository<Registro, UUID>, JpaSpecificationExecutor<Registro> {

    @Query("select r from Registro r left join fetch r.categorias where r.id = :id")
    Optional<Registro> buscarPorIdComCategorias(UUID id);

    @EntityGraph(attributePaths = "categorias")
    Page<Registro> findAll(Specification<Registro> specs, Pageable pageable);

    boolean existsByCategoriasContains(Categoria categoria);
}
