package com.example.DBEstudosAPI.service;

import com.example.DBEstudosAPI.controller.dto.CategoriaPatchDTO;
import com.example.DBEstudosAPI.controller.dto.CategoriaPostDTO;
import com.example.DBEstudosAPI.controller.dto.CategoriaResponseDTO;
import com.example.DBEstudosAPI.controller.mappers.CategoriaMapper;
import com.example.DBEstudosAPI.entities.Categoria;
import com.example.DBEstudosAPI.entities.Usuario;
import com.example.DBEstudosAPI.exceptions.CategoriaEmUsoException;
import com.example.DBEstudosAPI.exceptions.CategoriaNaoEncontradaException;
import com.example.DBEstudosAPI.exceptions.CategoriaNaoPermitidaException;
import com.example.DBEstudosAPI.repository.CategoriaRepository;
import com.example.DBEstudosAPI.repository.RegistroRepository;
import com.example.DBEstudosAPI.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CategoriaService {

    private final CategoriaRepository categoriaRepository;
    private final RegistroRepository registroRepository;
    private final CategoriaMapper mapper;
    private final UsuarioRepository usuarioRepository;

    @Transactional
    public CategoriaResponseDTO save(CategoriaPostDTO dto){
        Categoria categoria = mapper.toEntity(dto);
        UUID id = UUID.fromString(SecurityContextHolder.getContext().getAuthentication().getName());
        Usuario usuario = usuarioRepository.findById(id).orElseThrow(() -> new RuntimeException("Usuario não encontrado."));
        categoria.setUsuario(usuario);
        Categoria categoriaSalva = categoriaRepository.save(categoria);
        return mapper.toDTO(categoriaSalva);
    }

    public Categoria findEntityById(UUID id){
        return findOwnerCategory(id);
    }

    public CategoriaResponseDTO findById(UUID id){
        Categoria categoria = findEntityById(id);
        return mapper.toDTO(categoria);
    }

    public Set<CategoriaResponseDTO> search(String nomeCategoria){
        if(nomeCategoria == null){
            throw new CategoriaNaoEncontradaException("Parametro inválido!");
        }
        UUID id = UUID.fromString(SecurityContextHolder.getContext().getAuthentication().getName());
        return categoriaRepository.findAllByNomeCategoriaContainingIgnoreCaseAndUsuarioId(nomeCategoria, id)
                .stream()
                .map(mapper::toDTO)
                .collect(Collectors.toSet());
    }

    @Transactional
    public void delete(UUID id){
        Categoria categoria = findEntityById(id);
        if(existRegistro(categoria)){
            throw new CategoriaEmUsoException("Não é possivel deletar uma categoria que esteja sendo usada por um registro.");
        }
        categoriaRepository.delete(categoria);
    }

    @Transactional
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

    private Categoria findOwnerCategory(UUID id){
        UUID idUsuario = UUID.fromString(SecurityContextHolder.getContext().getAuthentication().getName());
        Categoria categoria = categoriaRepository.findById(id).orElseThrow(() -> new CategoriaNaoEncontradaException("Categoria não encontrada."));
        if(!categoria.getUsuario().getId().equals(idUsuario)){
            throw new CategoriaNaoEncontradaException("Categoria não encontrada.");
        }
        return categoria;
    }
}
