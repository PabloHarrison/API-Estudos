package com.example.DBEstudosAPI.exceptions.common;


import org.springframework.http.HttpStatus;

import java.util.Set;

public record RestMenssagemErro(HttpStatus status, String mensagem, Set<RestCampoErro> erros) {
}
