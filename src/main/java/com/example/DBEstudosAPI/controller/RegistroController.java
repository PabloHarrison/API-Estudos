package com.example.DBEstudosAPI.controller;

import com.example.DBEstudosAPI.controller.dto.RegistroPatchDTO;
import com.example.DBEstudosAPI.controller.dto.RegistroPostDTO;
import com.example.DBEstudosAPI.controller.dto.RegistroResponseDTO;
import com.example.DBEstudosAPI.service.RegistroService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("registros")
@RequiredArgsConstructor
public class RegistroController {

    private final RegistroService service;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public void salvar(@RequestBody @Valid RegistroPostDTO postDTO){
        service.save(postDTO);
    }

    @GetMapping("{id}")
    public ResponseEntity<RegistroResponseDTO> buscarPorId(@PathVariable("id") UUID id){
        RegistroResponseDTO dto = service.findById(id);
        return ResponseEntity.ok(dto);
    }

    @GetMapping
    public ResponseEntity<Page<RegistroResponseDTO>> search(
            @RequestParam(value = "ano", required = false) Integer ano,
            @RequestParam(value = "mes", required = false) Integer mes,
            @RequestParam(value = "dia", required = false) Integer dia,
            @RequestParam(value = "nome-categoria", required = false) String nomeCategoria,
            @RequestParam(value = "min", required = false) Integer min,
            @RequestParam(value = "max", required = false) Integer max,
            @RequestParam(value = "pagina", defaultValue = "0") Integer pagina,
            @RequestParam(value = "tamanho-paginas", defaultValue = "5") Integer tamanhoPagina
    ){
        Page<RegistroResponseDTO> resultado = service.search(ano, mes, dia, nomeCategoria, min, max, pagina, tamanhoPagina);
        return ResponseEntity.ok(resultado);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable("id") String id){
        service.delete(UUID.fromString(id));
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("{id}")
    public ResponseEntity<RegistroResponseDTO> update(@PathVariable("id") String id, @RequestBody RegistroPatchDTO dto){
        return ResponseEntity.ok(service.update(id, dto));
    }
}
