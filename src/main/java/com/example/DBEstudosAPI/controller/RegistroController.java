package com.example.DBEstudosAPI.controller;

import com.example.DBEstudosAPI.controller.dto.RegistroPatchDTO;
import com.example.DBEstudosAPI.controller.dto.RegistroPostDTO;
import com.example.DBEstudosAPI.controller.dto.RegistroResponseDTO;
import com.example.DBEstudosAPI.service.RegistroService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@Tag(name = "Registro")
public class RegistroController {

    private final RegistroService service;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(
            summary = "Criar novo registro",
            description = "Cria um novo registro a partir dos dados informados no corpo da requisição.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Registro criado com sucesso."),
            @ApiResponse(responseCode = "401", description = "Sessão inválida ou expirada."),
            @ApiResponse(responseCode = "400", description = "Erro de validação ou JSON inválido."),
            @ApiResponse(responseCode = "404", description = "Uma ou mais categorias não existem.")
    })
    public void salvar(@RequestBody @Valid RegistroPostDTO postDTO){
        service.save(postDTO);
    }

    @GetMapping("{id}")
    @Operation(
            summary = "Buscar registro pelo ID",
            description = "Retorna um registro a partir do ID informado.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Registro retornado com sucesso"),
            @ApiResponse(responseCode = "401", description = "Sessão inválida ou expirada"),
            @ApiResponse(responseCode = "404", description = "Registro não encontrado.")
    })
    public ResponseEntity<RegistroResponseDTO> buscarPorId(@PathVariable("id") UUID id){
        RegistroResponseDTO dto = service.findById(id);
        return ResponseEntity.ok(dto);
    }

    @GetMapping
    @Operation(
            summary = "Buscar registros com filtros e paginação",
            description = "Retorna uma lista paginada de registros com base nos filtros informados. " +
                    "Caso nenhum registro seja encontrado, retorna uma lista vazia.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Registros retornados com sucesso."),
            @ApiResponse(responseCode = "401", description = "Sessão inválida ou expirada."),
    })
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
    @Operation(
            summary = "Deletar registro pelo ID",
            description = "Deleta um registro a partir do ID informado.")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Registro deletado com sucesso."),
            @ApiResponse(responseCode = "401", description = "Sessão inválida ou expirada."),
            @ApiResponse(responseCode = "404", description = "Registro não encontrado")
    })
    public ResponseEntity<Void> deletar(@PathVariable("id") String id){
        service.delete(UUID.fromString(id));
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("{id}")
    @Operation(
            summary = "Atualizar registro pelo ID",
            description = "Atualiza parcialmente um registro com base no ID informado. Apenas os campos enviados no corpo da requisição serão modificados.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Registro atualizado com sucesso."),
            @ApiResponse(responseCode = "400", description = "Erro de validação ou JSON inválido."),
            @ApiResponse(responseCode = "401", description = "Sessão inválida ou expirada."),
            @ApiResponse(responseCode = "404", description = "Categoria não encontrada.")
    })
    public ResponseEntity<RegistroResponseDTO> update(@PathVariable("id") String id, @RequestBody RegistroPatchDTO dto){
        return ResponseEntity.ok(service.update(id, dto));
    }
}
