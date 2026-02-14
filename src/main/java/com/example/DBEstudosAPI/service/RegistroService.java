package com.example.DBEstudosAPI.service;

import com.example.DBEstudosAPI.controller.dto.RegistroPatchDTO;
import com.example.DBEstudosAPI.controller.dto.RegistroPostDTO;
import com.example.DBEstudosAPI.controller.dto.RegistroResponseDTO;
import com.example.DBEstudosAPI.controller.mappers.RegistroMapper;
import com.example.DBEstudosAPI.entities.Categoria;
import com.example.DBEstudosAPI.entities.Registro;
import com.example.DBEstudosAPI.entities.Usuario;
import com.example.DBEstudosAPI.exceptions.CategoriaNaoEncontradaException;
import com.example.DBEstudosAPI.exceptions.RegistroNaoEncontradoException;
import com.example.DBEstudosAPI.repository.CategoriaRepository;
import com.example.DBEstudosAPI.repository.RegistroRepository;
import com.example.DBEstudosAPI.repository.UsuarioRepository;
import com.example.DBEstudosAPI.repository.specs.RegistroSpecs;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class RegistroService {

    private final RegistroRepository repository;
    private final CategoriaRepository categoriaRepository;
    private final RegistroMapper registroMapper;
    private final UsuarioRepository usuarioRepository;

    @Transactional
    public RegistroResponseDTO save(RegistroPostDTO dto){
        Registro registro = registroMapper.toEntity(dto);
        UUID id = UUID.fromString(SecurityContextHolder.getContext().getAuthentication().getName());
        Usuario usuario = usuarioRepository.findById(id).orElseThrow(() -> new RuntimeException("Usuario não encontrado."));
        Set<Categoria> categorias = new HashSet<>(categoriaRepository.findAllByIdInAndUsuarioId(dto.categoriasIds(), usuario.getId()));
        if(categorias.size() != new HashSet<>(dto.categoriasIds()).size()){
            throw new CategoriaNaoEncontradaException("Uma ou mais categorias não existem!");
        }
        registro.setCategorias(categorias);
        registro.setUsuario(usuario);
        Registro registroSalvo = repository.save(registro);
        return registroMapper.toDTO(registroSalvo);
    }

    public RegistroResponseDTO findById(UUID id){
        Registro registro = repository.buscarPorIdComCategorias(id).orElseThrow(() -> new RegistroNaoEncontradoException("Registro não encontrado!"));
        UUID idUsuario = UUID.fromString(SecurityContextHolder.getContext().getAuthentication().getName());
        if(!registro.getUsuario().getId().equals(idUsuario)){
            throw new RegistroNaoEncontradoException("Registro não encontrado.");
        }
        return registroMapper.toDTO(registro);
    }

    @Transactional
    public void delete(UUID id){
        Registro registro = findOwnerRegistration(id);
        repository.delete(registro);
    }

    public Page<RegistroResponseDTO> search(Integer ano, Integer mes, Integer dia, String nomeCategoria, Integer min, Integer max, Integer pagina, Integer tamanhoPagina){

        UUID id = UUID.fromString(SecurityContextHolder.getContext().getAuthentication().getName());

        Specification<Registro> specs = RegistroSpecs.usuarioIdEquals(id);

        if(ano != null && mes != null && dia != null){
            specs = specs.and(RegistroSpecs.dataAnoMesDiaEquals(ano, mes, dia));
        }
        else if(ano != null && mes != null){
            specs = specs.and(RegistroSpecs.dataAnoMesEquals(ano, mes));
        }
        else if(ano != null ){
            specs = specs.and(RegistroSpecs.dataAnoEquals(ano));
        }
        if(nomeCategoria != null){
            specs = specs.and(RegistroSpecs.nomeCategoriaLike(nomeCategoria));
        }
        if(min != null || max != null){
            specs = specs.and(RegistroSpecs.tempoBetween(min, max));
        }
        Pageable pageRequest = PageRequest.of(pagina, tamanhoPagina);
        return repository.findAll(specs, pageRequest).map(registroMapper::toDTO);
    }

    @Transactional
    public RegistroResponseDTO update(String id, RegistroPatchDTO dto){
        UUID idNovo = UUID.fromString(id);
        Registro registro = findOwnerRegistration(idNovo);

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
            Set<Categoria> categorias = new HashSet<>(categoriaRepository.findAllByIdInAndUsuarioId(dto.categoriasIds(), registro.getUsuario().getId()));

            if(categorias.size() != dto.categoriasIds().size()){
                throw new CategoriaNaoEncontradaException("Categoria nao encontrada.");
            }

            registro.setCategorias(categorias);
        }

        repository.save(registro);
        return registroMapper.toDTO(registro);
    }

    private Registro findOwnerRegistration(UUID id){
        UUID idUsuario = UUID.fromString(SecurityContextHolder.getContext().getAuthentication().getName());
        Registro registro = repository.findById(id).orElseThrow(() -> new RegistroNaoEncontradoException("Registro não encontrado."));
        if(!registro.getUsuario().getId().equals(idUsuario)){
            throw new RegistroNaoEncontradoException("Registro não encontrado.");
        }
        return registro;
    }
}
