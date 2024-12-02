package com.esp.espflow.service.respository.impl;

import com.esp.espflow.entity.EsptoolExecutableEntity;
import com.esp.espflow.entity.dto.EsptoolExecutableDto;
import com.esp.espflow.mappers.EsptoolExecutableMapper;
import com.esp.espflow.service.respository.EsptoolExecutableRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

/**
 * @author rubn
 */
@Log4j2
@Service
@RequiredArgsConstructor
public class EsptoolExecutableServiceImpl {

    private final EsptoolExecutableRepository esptoolExecutableRepository;

    /**
     * Saves the EsptoolExecutableEntity
     *
     * @param esptoolExecutableDto to save
     */
    public EsptoolExecutableDto save(EsptoolExecutableDto esptoolExecutableDto) {
        var mappedEntity = EsptoolExecutableMapper.INSTANCE.dtoToEntity(esptoolExecutableDto);
        EsptoolExecutableEntity entitySaved = this.esptoolExecutableRepository.save(mappedEntity);
        log.info("Entity saved EsptoolExecutableEntity: {}", entitySaved);
        return EsptoolExecutableMapper.INSTANCE.entityToDto(entitySaved);
    }

    public Optional<EsptoolExecutableDto> findById(Long id) {
        return this.esptoolExecutableRepository.findById(id)
                .map(EsptoolExecutableMapper.INSTANCE::entityToDto);
    }

    public Optional<EsptoolExecutableDto> findByEsptoolVersionWithBundle(String esptoolVersion, boolean isBundle) {
        return this.esptoolExecutableRepository.findByEsptoolVersionWithBundle(esptoolVersion, isBundle)
                .map(EsptoolExecutableMapper.INSTANCE::entityToDto);
    }

    @Transactional
    public void updateAllSelectedToFalseExcept(final long id) {
        this.esptoolExecutableRepository.updateAllSelectedToFalseExcept(id);
    }

    public Optional<EsptoolExecutableDto> findByIsSelectedToTrue() {
        return this.esptoolExecutableRepository.findByIsSelectedToTrue()
                .map(EsptoolExecutableMapper.INSTANCE::entityToDto);
    }

    public void deleteById(final long id) {
        this.esptoolExecutableRepository.deleteById(id);
    }

    public Optional<EsptoolExecutableDto> findByAbsolutePathEsptoolAndIsBundle(String absolutePathEsptool, boolean isBundle) {
        return this.esptoolExecutableRepository.findByAbsolutePathEsptoolAndIsBundle(absolutePathEsptool, isBundle)
                .map(EsptoolExecutableMapper.INSTANCE::entityToDto);
    }

    public List<EsptoolExecutableDto> findAll() {
        return this.esptoolExecutableRepository.findAll()
                .stream()
                .map(EsptoolExecutableMapper.INSTANCE::entityToDto)
                //.peek(dto -> log.info("Dto {}", dto))
                .toList();
    }

}
