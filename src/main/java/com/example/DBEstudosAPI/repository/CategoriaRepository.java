package com.example.DBEstudosAPI.repository;

import com.example.DBEstudosAPI.entities.Categoria;
import com.example.DBEstudosAPI.entities.Registro;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Set;
import java.util.UUID;

public interface CategoriaRepository extends JpaRepository<Categoria, UUID> {

    Set<Categoria> findAllByNomeCategoriaContainingIgnoreCase(String nomeCategoria);
}
