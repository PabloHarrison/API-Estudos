package com.example.DBEstudosAPI.entities;

import com.example.DBEstudosAPI.enums.Roles;
import jakarta.persistence.*;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Entity
@Table
@Getter
@Setter
public class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    @Column(nullable = false, length = 20)
    private String login;
    @Column(nullable = false, unique = true, length = 100)
    private String email;
    @Column(nullable = false, length = 64)
    private String password;
    @Column
    @Enumerated(EnumType.STRING)
    private Roles roles;
}
