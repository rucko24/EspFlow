package com.esp.espflow.service.respository.impl;

import com.esp.espflow.dto.EsptoolExecutableDto;
import com.esp.espflow.entity.EsptoolExecutableEntity;
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
public class EsptoolExecutableService {

    private final EsptoolExecutableRepository esptoolExecutableRepository;

    /**
     * Used to select the new version of esptool.py
     *
     * @param esptoolExecutableDto
     */
    @Transactional
    public void selectThisEsptoolExecutableVersion(EsptoolExecutableDto esptoolExecutableDto) {
        this.save(esptoolExecutableDto);
        this.updateAllSelectedToFalseExcept(esptoolExecutableDto.id());
    }

    /**
     * Saves the EsptoolExecutableEntity, in case of upgrading, all are marked false except the entity to be upgraded
     *
     * @param esptoolExecutableDto to save
     *
     * @return A {@link EsptoolExecutableDto}
     */
    public EsptoolExecutableDto save(EsptoolExecutableDto esptoolExecutableDto) {
        var mappedEntity = EsptoolExecutableMapper.INSTANCE.dtoToEntity(esptoolExecutableDto);
        this.esptoolExecutableRepository.findByEsptoolVersionWithBundle(mappedEntity.getEsptoolVersion(), mappedEntity.isBundled())
                .ifPresentOrElse(entityIfPresent -> {
                    final EsptoolExecutableEntity entityToUpdate = EsptoolExecutableMapper.INSTANCE.fromEntityPresent(entityIfPresent.getId(), mappedEntity);
                    final EsptoolExecutableEntity updatedEntity = this.esptoolExecutableRepository.save(entityToUpdate);
                    log.info("Entity updated {}", updatedEntity);
                }, () -> {
                    final EsptoolExecutableEntity entitySaved = this.esptoolExecutableRepository.save(mappedEntity);
                    log.info("Entity saved {}", entitySaved);
                });
        return EsptoolExecutableMapper.INSTANCE.entityToDto(mappedEntity);
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

    public Optional<EsptoolExecutableDto> findByAbsolutePathEsptoolAndIsBundleAndVersion(String absolutePathEsptool, boolean isBundle,
                                                                                         String esptoolVersion) {
        return this.esptoolExecutableRepository.findByAbsolutePathEsptoolAndIsBundleAndVersion(absolutePathEsptool, isBundle, esptoolVersion)
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
