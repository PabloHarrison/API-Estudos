package com.example.DBEstudosAPI.repository.specs;

import com.example.DBEstudosAPI.entities.Registro;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDate;

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

    public static Specification<Registro> dataAnoMesDiaEquals(Integer ano, Integer mes, Integer dia){
        return (root, query, cb) -> {
            if (ano == null || mes == null || dia == null) {
                return cb.conjunction();
            }

            LocalDate data = LocalDate.of(ano, mes, dia);

            return cb.equal(root.get("data"), data);
        };
    }

    public static Specification<Registro> tempoBetween(Integer min, Integer max){
        return (root, query, cb) -> {
            if(min == null && max == null){
                return cb.conjunction();
            }
            if(min != null && max != null){
                return cb.between(root.get("horasEstudadas"), min, max);
            }
            if(min != null){
                return cb.greaterThanOrEqualTo(root.get("horasEstudadas"), min);
            }
            return cb.lessThanOrEqualTo(root.get("horasEstudadas"), max);
        };
    }

    public static Specification<Registro> nomeCategoriaLike(String nomeCategoria){
        return (root, query, cb) -> {
            query.distinct(true);
            Join<Object, Object> joinCategoria = root.join("categorias", JoinType.LEFT);
            return cb.like(cb.upper(joinCategoria.get("nomeCategoria")), "%" + nomeCategoria.toUpperCase() + "%");
        };
    }
}
