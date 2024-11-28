package com.esp.espflow.service.respository.impl;

import com.esp.espflow.entity.dto.EsptoolExecutableDto;
import com.esp.espflow.mappers.EsptoolBundleMapper;
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
    public void save(EsptoolExecutableDto esptoolExecutableDto) {
        var mappedEntity = EsptoolBundleMapper.INSTANCE.dtoToEntity(esptoolExecutableDto);
        this.esptoolExecutableRepository.save(mappedEntity);
        log.info("Entity saved EsptoolExecutableEntity: {}", mappedEntity);
    }

    public Optional<EsptoolExecutableDto> findById(Long id) {
        return this.esptoolExecutableRepository.findById(id)
                .map(EsptoolBundleMapper.INSTANCE::entityToDto);
    }

    @Transactional
    public void updateAllSelectedToFalseExcept(final long id) {
        this.esptoolExecutableRepository.updateAllSelectedToFalseExcept(id);
    }

    public Optional<EsptoolExecutableDto> findByIsSelectedToTrue() {
        return this.esptoolExecutableRepository.findByIsSelectedToTrue()
                .map(EsptoolBundleMapper.INSTANCE::entityToDto);
    }

    public Optional<EsptoolExecutableDto> findByAbsolutePathEsptoolAndEsptoolVersion(String absolutePathEsptool, String esptoolVersion) {
        return this.esptoolExecutableRepository.findByAbsolutePathEsptoolAndEsptoolVersion(absolutePathEsptool, esptoolVersion)
                .map(EsptoolBundleMapper.INSTANCE::entityToDto);
    }

    public List<EsptoolExecutableDto> findAll() {
        return this.esptoolExecutableRepository.findAll()
                .stream()
                .map(EsptoolBundleMapper.INSTANCE::entityToDto)
                .peek(dto -> log.info("Dto {}", dto))
                .toList();
    }

}
