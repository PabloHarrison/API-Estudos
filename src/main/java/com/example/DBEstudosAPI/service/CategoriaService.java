package com.example.DBEstudosAPI.service;

import com.example.DBEstudosAPI.controller.dto.CategoriaPatchDTO;
import com.example.DBEstudosAPI.controller.dto.CategoriaPostDTO;
import com.example.DBEstudosAPI.controller.dto.CategoriaResponseDTO;
import com.example.DBEstudosAPI.controller.mappers.CategoriaMapper;
import com.example.DBEstudosAPI.entities.Categoria;
import com.example.DBEstudosAPI.entities.Registro;
import com.example.DBEstudosAPI.exceptions.CategoriaEmUsoException;
import com.example.DBEstudosAPI.exceptions.CategoriaNaoEncontradaException;
import com.example.DBEstudosAPI.exceptions.CategoriaNaoPermitidaException;
import com.example.DBEstudosAPI.repository.CategoriaRepository;
import com.example.DBEstudosAPI.repository.RegistroRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CategoriaService {

    private final CategoriaRepository categoriaRepository;
    private final RegistroRepository registroRepository;
    private final CategoriaMapper mapper;

    public Categoria save(CategoriaPostDTO dto){
        Categoria categoria = mapper.toEntity(dto);
        return categoriaRepository.save(categoria);
    }

    public Categoria findEntityById(UUID id){
        return categoriaRepository.findById(id).orElseThrow(() -> new CategoriaNaoEncontradaException("Categoria não encontrada."));
    }

    public CategoriaResponseDTO findById(UUID id){
        var categoria = categoriaRepository.findById(id).orElseThrow(() -> new CategoriaNaoEncontradaException("Categoria não encontrada!"));
        return mapper.toDTO(categoria);
    }

    public Set<CategoriaResponseDTO> search(String nomeCategoria){
        Set<Categoria> categoriaEncontrada = categoriaRepository.findAllByNomeCategoriaContainingIgnoreCase(nomeCategoria);
        if(nomeCategoria == null){
            throw new CategoriaNaoEncontradaException("Parametro inválido!");
        }
        return categoriaEncontrada
                .stream()
                .map(mapper::toDTO).collect(Collectors.toSet());
    }

    public void delete(UUID id){
        Categoria categoria = findEntityById(id);
        if(existRegistro(categoria)){
            throw new CategoriaEmUsoException("Não é possivel deletar uma categoria que esteja sendo usada por um registro.");
        }
        categoriaRepository.delete(categoria);
    }

    public CategoriaResponseDTO update(UUID id, CategoriaPatchDTO dto){
        Categoria categoria = findEntityById(id);
        if(dto.nomeCategoria() == null || dto.nomeCategoria().isBlank()){
            throw new CategoriaNaoPermitidaException("É obrigatorio nomear a categoria!");
        }else{
            categoria.setNomeCategoria(dto.nomeCategoria());
        }
        categoriaRepository.save(categoria);
        return mapper.toDTO(categoria);
    }

    public boolean existRegistro(Categoria categoria){
        return registroRepository.existsByCategoriasContains(categoria);
    }
}
