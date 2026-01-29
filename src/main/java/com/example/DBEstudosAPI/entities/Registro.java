package com.example.DBEstudosAPI.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.time.LocalDate;
import java.util.Set;
import java.util.UUID;

@Entity
@Getter
@Setter
public class Registro {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    @Column(nullable = false)
    private LocalDate data;
    @Column(nullable = false)
    private Integer horasEstudadas;
    private String anotacao;
    private String resumo;
    private String planejamento;
    @JoinTable(name = "registro_categorias", joinColumns = @JoinColumn(name = "registro_id"), inverseJoinColumns = @JoinColumn(name = "categoria_id"))
    @ManyToMany
    private Set<Categoria> categorias;
}
