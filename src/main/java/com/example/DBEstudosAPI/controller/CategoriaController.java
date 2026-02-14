package com.example.DBEstudosAPI.controller;

import com.example.DBEstudosAPI.controller.dto.CategoriaPatchDTO;
import com.example.DBEstudosAPI.controller.dto.CategoriaPostDTO;
import com.example.DBEstudosAPI.controller.dto.CategoriaResponseDTO;
import com.example.DBEstudosAPI.service.CategoriaService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@Tag(name = "Categoria")
public class CategoriaController {

    private final CategoriaService service;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(
            summary = "Criar nova categoria",
            description = "Cria uma nova categoria a partir do dado informado no corpo da requisição.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Categoria criado com sucesso."),
            @ApiResponse(responseCode = "400", description = "Erro de validação ou JSON inválido."),
            @ApiResponse(responseCode = "401", description = "Sessão inválida ou expirada.")
    })
    public void salvar(@RequestBody @Valid CategoriaPostDTO dto){
        service.save(dto);
    }

    @GetMapping("{id}")
    @Operation(
            summary = "Buscar categoria pelo ID",
            description = "Retorna uma categoria a partir do ID informado.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Categoria retornado com sucesso"),
            @ApiResponse(responseCode = "401", description = "Sessão inválida ou expirada"),
            @ApiResponse(responseCode = "404", description = "Categoria não encontrado.")
    })
    public ResponseEntity<CategoriaResponseDTO> buscarPorId(@PathVariable("id") UUID id){
        return ResponseEntity.ok(service.findById(id));
    }

    @GetMapping
    @Operation(
            summary = "Buscar categorias por filtro de nome",
            description = "Retorna uma lista paginada de categorias com base no nome informado. " +
                    "Caso nenhuma categoria seja encontrada, retorna uma lista vazia.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Categorias retornadas com sucesso."),
            @ApiResponse(responseCode = "401", description = "Sessão inválida ou expirada."),
    })
    public ResponseEntity<Set<CategoriaResponseDTO>> pesquisa(@RequestParam(name = "nome-categoria", required = false) String nomeCategoria){
        Set<CategoriaResponseDTO> categoria = service.search(nomeCategoria);
        return ResponseEntity.ok(categoria);
    }

    @DeleteMapping("{id}")
    @Operation(
            summary = "Deletar categoria pelo ID",
            description = "Deleta uma categoria a partir do ID informado.")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Categoria deletada com sucesso."),
            @ApiResponse(responseCode = "401", description = "Sessão inválida ou expirada."),
            @ApiResponse(responseCode = "404", description = "Categoria não encontrado"),
            @ApiResponse(responseCode = "409", description = "Não é possivel deletar uma categoria que esteja sendo usada por um registro.")
    })
    public ResponseEntity<Void> deletar(@PathVariable("id") UUID id){
        service.delete(id);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("{id}")
    @Operation(
            summary = "Atualizar categoria pelo ID",
            description = "Atualiza o nome de uma categoria com base no ID informado e o campo enviado no corpo da requisição.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Categoria atualizada com sucesso."),
            @ApiResponse(responseCode = "400", description = "Obrigatório preencher o campo requisitado."),
            @ApiResponse(responseCode = "401", description = "Sessão inválida ou expirada."),
            @ApiResponse(responseCode = "404", description = "Categoria não encontrada.")
    })
    public ResponseEntity<CategoriaResponseDTO> atualizar(@PathVariable("id") UUID id,@RequestBody CategoriaPatchDTO nomeCategoria){
        return ResponseEntity.ok(service.update(id, nomeCategoria));
    }
}
