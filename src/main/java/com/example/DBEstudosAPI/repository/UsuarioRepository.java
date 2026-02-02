package com.example.DBEstudosAPI.repository;

import com.example.DBEstudosAPI.entities.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface UsuarioRepository extends JpaRepository<Usuario, UUID> {

    Usuario findByLogin(String login);
    Usuario findByEmail(String email);
}
