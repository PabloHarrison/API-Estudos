package com.example.DBEstudosAPI.validator;

import com.example.DBEstudosAPI.entities.Usuario;
import com.example.DBEstudosAPI.exceptions.EmailCadastradoException;
import com.example.DBEstudosAPI.exceptions.LoginCadastradoException;
import com.example.DBEstudosAPI.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class UsuarioValidator {

    @Autowired
    UsuarioRepository repository;

    public void usuarioValidator(String login, String email){
        Usuario foundUsuario = repository.findByLogin(login).orElse(null);
        if(foundUsuario != null){
            throw new LoginCadastradoException("Login cadastrado.");
        }
        foundUsuario = repository.findByEmail(email).orElse(null);
        if(foundUsuario != null){
            throw new EmailCadastradoException("Email cadastrado.");
        }
    }
}
