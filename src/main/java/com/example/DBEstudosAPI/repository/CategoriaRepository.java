package com.example.DBEstudosAPI.repository;

import com.example.DBEstudosAPI.entities.Categoria;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Set;
import java.util.UUID;

public interface CategoriaRepository extends JpaRepository<Categoria, UUID> {

    Set<Categoria> findAllByNomeCategoriaContainingIgnoreCaseAndUsuarioId(String nomeCategoria, UUID id);

    Set<Categoria> findAllByIdInAndUsuarioId(Set<UUID> ids, UUID usuarioId);

    Set<Categoria> findAllByUsuarioId(UUID usuarioId);

    @Query(
            value = """
                    SELECT COUNT(*)
                    FROM registro_categorias
                    WHERE categoria_id = :categoriaId
                    """,
            nativeQuery = true)
   Long countRegistrosByCategoriaId(UUID categoriaId);
}
