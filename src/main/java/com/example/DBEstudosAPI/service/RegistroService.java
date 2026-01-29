package com.example.DBEstudosAPI.service;

import com.example.DBEstudosAPI.controller.dto.RegistroPatchDTO;
import com.example.DBEstudosAPI.controller.dto.RegistroPostDTO;
import com.example.DBEstudosAPI.controller.dto.RegistroResponseDTO;
import com.example.DBEstudosAPI.controller.mappers.RegistroMapper;
import com.example.DBEstudosAPI.entities.Categoria;
import com.example.DBEstudosAPI.entities.Registro;
import com.example.DBEstudosAPI.exceptions.CategoriaNaoEncontradaException;
import com.example.DBEstudosAPI.exceptions.RegistroNaoEncontradoException;
import com.example.DBEstudosAPI.repository.CategoriaRepository;
import com.example.DBEstudosAPI.repository.RegistroRepository;
import com.example.DBEstudosAPI.repository.specs.RegistroSpecs;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class RegistroService {

    private final RegistroRepository repository;
    private final CategoriaRepository categoriaRepository;
    private final RegistroMapper registroMapper;

    public Registro save(RegistroPostDTO dto){
        Registro registro = registroMapper.toEntity(dto);
        Set<Categoria> categorias = new HashSet<>(categoriaRepository.findAllById(dto.categoriasIds()));

        if(categorias.size() != dto.categoriasIds().size()){
            throw new CategoriaNaoEncontradaException("Uma ou mais categorias não existem!");
        }
        registro.setCategorias(categorias);
        return repository.save(registro);
    }

    public RegistroResponseDTO findById(UUID id){
        var registro = repository.buscarPorIdComCategorias(id).orElseThrow(() -> new RegistroNaoEncontradoException("Registro não encontrado!"));
        return registroMapper.toDTO(registro);
    }

    public void delete(UUID id){
        Registro registro = findEntityById(id);
        repository.delete(registro);
    }

    public Registro findEntityById(UUID id){
        return repository.findById(id).orElseThrow(() -> new RegistroNaoEncontradoException("Registro não encontrado!"));

    }

    public Page<RegistroResponseDTO> search(Integer ano, Integer mes, Integer dia, String nomeCategoria, Integer min, Integer max, Integer pagina, Integer tamanhoPagina){

        Specification<Registro> specs = null;

        if(ano != null && mes != null && dia != null){
            specs = specs == null
                    ? RegistroSpecs.dataAnoMesDiaEquals(ano, mes, dia)
                    : specs.and(RegistroSpecs.dataAnoMesDiaEquals(ano, mes, dia));
        }
        else if(ano != null && mes != null){
            specs = specs == null
                    ? RegistroSpecs.dataAnoMesEquals(ano, mes)
                    : specs.and(RegistroSpecs.dataAnoMesEquals(ano, mes));
        }
        else if(ano != null ){
            specs = specs == null
                    ? RegistroSpecs.dataAnoEquals(ano)
                    : specs.and(RegistroSpecs.dataAnoEquals(ano));
        }
        if(nomeCategoria != null){
            specs = specs == null
                    ? RegistroSpecs.nomeCategoriaLike(nomeCategoria)
                    : specs.and(RegistroSpecs.nomeCategoriaLike(nomeCategoria));
        }
        if(min != null || max != null){
            specs = specs == null
                    ? RegistroSpecs.tempoBetween(min, max)
                    : specs.and(RegistroSpecs.tempoBetween(min, max));
        }

        Pageable pageRequest = PageRequest.of(pagina, tamanhoPagina);
        return repository.findAll(specs, pageRequest).map(registroMapper::toDTO);
    }

    public RegistroResponseDTO update(String id, RegistroPatchDTO dto){
        UUID idNovo = UUID.fromString(id);
        Registro registro = repository.buscarPorIdComCategorias(idNovo)
                .orElseThrow(() -> new RegistroNaoEncontradoException("Id não encontrado"));

        if(dto.data() != null){
            registro.setData(dto.data());
        }
        if(dto.horasEstudadas() != null){
            registro.setHorasEstudadas(dto.horasEstudadas());
        }
        if(dto.anotacao() != null){
            registro.setAnotacao(dto.anotacao());
        }
        if(dto.resumo() != null){
            registro.setResumo(dto.resumo());
        }
        if(dto.planejamento() != null){
            registro.setPlanejamento(dto.planejamento());
        }
        if(dto.categoriasIds() != null){
            Set<Categoria> categorias = new HashSet<>(categoriaRepository.findAllById(dto.categoriasIds()));

            if(categorias.size() != dto.categoriasIds().size()){
                throw new CategoriaNaoEncontradaException("Categoria nao encontrada.");
            }

            registro.setCategorias(categorias);
        }

        repository.save(registro);
        return registroMapper.toDTO(registro);

    }
}
