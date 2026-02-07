package com.example.DBEstudosAPI.repository;

import com.example.DBEstudosAPI.entities.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface UsuarioRepository extends JpaRepository<Usuario, UUID> {

    Optional<Usuario> findByLogin(String login);
    Optional<Usuario> findByEmail(String email);
}
