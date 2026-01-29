package com.example.DBEstudosAPI.controller;

import com.example.DBEstudosAPI.controller.dto.CategoriaPatchDTO;
import com.example.DBEstudosAPI.controller.dto.CategoriaPostDTO;
import com.example.DBEstudosAPI.controller.dto.CategoriaResponseDTO;
import com.example.DBEstudosAPI.service.CategoriaService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Set;
import java.util.UUID;

@RestController
@RequestMapping("categorias")
@RequiredArgsConstructor
public class CategoriaController {

    private final CategoriaService service;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public void salvar(@RequestBody @Valid CategoriaPostDTO dto){
        service.save(dto);
    }

    @GetMapping("{id}")
    public ResponseEntity<CategoriaResponseDTO> buscarPorId(@PathVariable("id") UUID id){
        return ResponseEntity.ok(service.findById(id));
    }

    @GetMapping
    public ResponseEntity<Set<CategoriaResponseDTO>> pesquisa(@RequestParam(name = "nome-categoria", required = false) String nomeCategoria){
        Set<CategoriaResponseDTO> categoria = service.search(nomeCategoria);
        return ResponseEntity.ok(categoria);
    }

    @DeleteMapping("{id}")
    public ResponseEntity<Void> deletar(@PathVariable("id") UUID id){
        service.delete(id);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("{id}")
    public ResponseEntity<CategoriaResponseDTO> atualizar(@PathVariable("id") UUID id,@RequestBody CategoriaPatchDTO nomeCategoria){
        return ResponseEntity.ok(service.update(id, nomeCategoria));
    }
}
