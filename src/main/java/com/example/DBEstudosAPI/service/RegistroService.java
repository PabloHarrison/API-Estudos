package com.example.DBEstudosAPI.service;

import com.example.DBEstudosAPI.dto.RegistroPatchDTO;
import com.example.DBEstudosAPI.dto.RegistroPostDTO;
import com.example.DBEstudosAPI.dto.RegistroResponseDTO;
import com.example.DBEstudosAPI.mappers.RegistroMapper;
import com.example.DBEstudosAPI.entities.Categoria;
import com.example.DBEstudosAPI.entities.Registro;
import com.example.DBEstudosAPI.entities.Usuario;
import com.example.DBEstudosAPI.exceptions.CategoriaNaoEncontradaException;
import com.example.DBEstudosAPI.exceptions.RegistroNaoEncontradoException;
import com.example.DBEstudosAPI.exceptions.UsuarioNaoEncontradoException;
import com.example.DBEstudosAPI.repository.CategoriaRepository;
import com.example.DBEstudosAPI.repository.RegistroRepository;
import com.example.DBEstudosAPI.repository.UsuarioRepository;
import com.example.DBEstudosAPI.repository.specs.RegistroSpecs;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class RegistroService {

    private final RegistroRepository repository;
    private final CategoriaRepository categoriaRepository;
    private final RegistroMapper registroMapper;
    private final UsuarioRepository usuarioRepository;
    private final AuthenticatedUserService authenticatedUserService;

    @Transactional
    public RegistroResponseDTO save(RegistroPostDTO dto) {
        Registro registro = registroMapper.toEntity(dto);
        UUID id = authenticatedUserService.getCurrentUserId();
        Usuario usuario = usuarioRepository.findById(id).orElseThrow(() -> new UsuarioNaoEncontradoException("Usuario não encontrado."));
        Set<Categoria> categorias = new HashSet<>(categoriaRepository.findAllByIdInAndUsuarioId(dto.categoriasIds(), usuario.getId()));
        if (categorias.size() != new HashSet<>(dto.categoriasIds()).size()) {
            throw new CategoriaNaoEncontradaException("Uma ou mais categorias não existem!");
        }
        registro.setCategorias(categorias);
        registro.setUsuario(usuario);
        Registro registroSalvo = repository.save(registro);
        log.info("event=registro_created registroId={} usuarioId={} categoriasCount={}", registroSalvo.getId(), id, categorias.size());
        return registroMapper.toDTO(registroSalvo);
    }

    public RegistroResponseDTO findById(UUID id) {
        Registro registro = repository.buscarPorIdComCategorias(id).orElseThrow(() -> new RegistroNaoEncontradoException("Registro não encontrado!"));
        UUID idUsuario = authenticatedUserService.getCurrentUserId();
        if (!registro.getUsuario().getId().equals(idUsuario)) {
            throw new RegistroNaoEncontradoException("Registro não encontrado.");
        }
        return registroMapper.toDTO(registro);
    }

    @Transactional
    public void delete(UUID id) {
        Registro registro = findOwnerRegistration(id);
        repository.delete(registro);
        log.info("event=registro_deleted registroId={} usuarioId={}", registro.getId(), registro.getUsuario().getId());
    }

    public Page<RegistroResponseDTO> search(LocalDate dataEspecifica, LocalDate dataInicio, LocalDate dataFim, Integer ano, Integer mes, String nomeCategoria, Integer min, Integer max, Integer pagina, Integer tamanhoPagina, String ordernarPor) {
        UUID id = authenticatedUserService.getCurrentUserId();
        Specification<Registro> specs = RegistroSpecs.usuarioIdEquals(id);

        if ((dataInicio == null) != (dataFim == null)){
            throw new IllegalArgumentException("Data de início e data de fim devem ser informadas juntas.");
        }
        if (dataInicio != null && dataInicio.isAfter(dataFim)){
            throw new IllegalArgumentException("A data inicial não pode ser posterior à data final.");
        }

        if (dataEspecifica != null) {
            specs = specs.and(RegistroSpecs.dataEquals(dataEspecifica));
        } else if (dataInicio != null && dataFim != null) {
            specs = specs.and(RegistroSpecs.dataBetween(dataInicio, dataFim));
        } else if (ano != null && mes != null) {
            specs = specs.and(RegistroSpecs.dataAnoMesEquals(ano, mes));
        } else if (ano != null) {
            specs = specs.and(RegistroSpecs.dataAnoEquals(ano));
        }
        if (nomeCategoria != null) {
            specs = specs.and(RegistroSpecs.nomeCategoriaLike(nomeCategoria));
        }
        if (min != null || max != null) {
            specs = specs.and(RegistroSpecs.tempoBetween(min, max));
        }

        Pageable pageRequest = orderBy(pagina, tamanhoPagina, ordernarPor);

        return repository.findAll(specs, pageRequest).map(registroMapper::toDTO);
    }

    @Transactional
    public RegistroResponseDTO update(UUID id, RegistroPatchDTO dto) {
        Registro registro = findOwnerRegistration(id);

        if (dto.data() != null) {
            registro.setData(dto.data());
        }
        if (dto.tempoEmMinutos() != null) {
            registro.setTempoEmMinutos(dto.tempoEmMinutos());
        }
        if (dto.resumo() != null) {
            registro.setResumo(dto.resumo());
        }
        if (dto.planejamento() != null) {
            registro.setPlanejamento(dto.planejamento());
        }
        if (dto.categoriasIds() != null) {
            Set<Categoria> categorias = new HashSet<>(categoriaRepository.findAllByIdInAndUsuarioId(dto.categoriasIds(), registro.getUsuario().getId()));

            if (categorias.size() != dto.categoriasIds().size()) {
                throw new CategoriaNaoEncontradaException("Categoria nao encontrada.");
            }

            registro.setCategorias(categorias);
        }

        repository.save(registro);
        log.info("event=registro_updated registroId={} usuarioId={}", registro.getId(), registro.getUsuario().getId());
        return registroMapper.toDTO(registro);
    }

    private Registro findOwnerRegistration(UUID id) {
        UUID idUsuario = authenticatedUserService.getCurrentUserId();
        Registro registro = repository.findById(id).orElseThrow(() -> new RegistroNaoEncontradoException("Registro não encontrado."));
        if (!registro.getUsuario().getId().equals(idUsuario)) {
            throw new RegistroNaoEncontradoException("Registro não encontrado.");
        }
        return registro;
    }

    private Pageable orderBy(Integer pagina, Integer tamanhoPagina, String ordenarPor){
        return switch (ordenarPor) {
            case "older" -> PageRequest.of(pagina, tamanhoPagina, Sort.by("data").ascending());
            case "more-time" -> PageRequest.of(pagina, tamanhoPagina, Sort.by("tempoEmMinutos").descending());
            case "less-time" -> PageRequest.of(pagina, tamanhoPagina, Sort.by("tempoEmMinutos").ascending());
            default -> PageRequest.of(pagina, tamanhoPagina, Sort.by("data").descending());
        };
    }
}
