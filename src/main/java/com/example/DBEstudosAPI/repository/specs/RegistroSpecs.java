package com.example.DBEstudosAPI.repository.specs;

import com.example.DBEstudosAPI.entities.Registro;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDate;
import java.util.UUID;

public class RegistroSpecs {

    public static Specification<Registro> dataAnoEquals(Integer ano){
        return (root, query, cb) -> {
            LocalDate inicio = LocalDate.of(ano, 1, 1);
            LocalDate fim = inicio.plusYears(1);

            return cb.and(
                    cb.greaterThanOrEqualTo(root.get("data"), inicio),
                    cb.lessThan(root.get("data"), fim)
            );
        };
    }

    public static Specification<Registro> dataAnoMesEquals(Integer ano, Integer mes){
        return (root, query, cb) -> {
            if (ano == null || mes == null) {
                return cb.conjunction();
            }

            LocalDate inicio = LocalDate.of(ano, mes, 1);
            LocalDate fim = inicio.plusMonths(1);

            return cb.and(
                    cb.greaterThanOrEqualTo(root.get("data"), inicio),
                    cb.lessThan(root.get("data"), fim)
            );
        };
    }

    public static Specification<Registro> dataEquals(LocalDate data){
        return (root, query, cb) -> {
            if (data == null) {
                return cb.conjunction();
            }
            return cb.equal(root.get("data"), data);
        };
    }

    public static Specification<Registro> dataBetween(LocalDate inicio, LocalDate fim){
        return (root, query, cb) -> cb.between(
                root.get("data"), inicio, fim);
    }

    public static Specification<Registro> tempoBetween(Integer min, Integer max){
        return (root, query, cb) -> {
            if(min == null && max == null){
                return cb.conjunction();
            }
            if(min != null && max != null){
                return cb.between(root.get("tempoEmMinutos"), min, max);
            }
            if(min != null){
                return cb.greaterThanOrEqualTo(root.get("tempoEmMinutos"), min);
            }
            return cb.lessThanOrEqualTo(root.get("tempoEmMinutos"), max);
        };
    }

    public static Specification<Registro> nomeCategoriaLike(String nomeCategoria){
        return (root, query, cb) -> {
            query.distinct(true);
            Join<Object, Object> joinCategoria = root.join("categorias", JoinType.LEFT);
            return cb.like(cb.upper(joinCategoria.get("nomeCategoria")), "%" + nomeCategoria.toUpperCase() + "%");
        };
    }

    public static Specification<Registro> usuarioIdEquals(UUID id){
        return (root, query, cb) ->
                cb.equal(root.get("usuario").get("id"), id);
    }
}
