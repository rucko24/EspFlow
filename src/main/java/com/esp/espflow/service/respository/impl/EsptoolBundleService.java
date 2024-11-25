package com.esp.espflow.service.respository.impl;

import com.esp.espflow.entity.dto.EsptoolBundleDto;
import com.esp.espflow.mappers.EsptoolBundleMapper;
import com.esp.espflow.service.respository.EsptoolBundleRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;

import java.util.Optional;

/**
 * @author rubn
 */
@Log4j2
@Service
@RequiredArgsConstructor
public class EsptoolBundleService {

    private final EsptoolBundleRepository esptoolBundleRepository;

    /**
     * Saves the EsptoolBundleEntity or updates it if it exists
     *
     * @param esptoolBundleDto to save
     */
    public void save(EsptoolBundleDto esptoolBundleDto) {
        var entity = EsptoolBundleMapper.INSTANCE.dtoToEntity(esptoolBundleDto);
        this.esptoolBundleRepository.findById(entity.getId())
                .ifPresentOrElse(entityIfPresent -> {
                    entityIfPresent.setInBundleMode(entity.isInBundleMode());
                    esptoolBundleRepository.save(entityIfPresent);
                    log.info("Entity updated {}", entityIfPresent);
                }, () -> {
                    esptoolBundleRepository.save(entity);
                    log.info("Entity saved {}", entity);
                });
    }

    public Optional<EsptoolBundleDto> findByIsInBundleMode(boolean isBundle) {
        return this.esptoolBundleRepository.findByIsInBundleMode(isBundle)
                .map(EsptoolBundleMapper.INSTANCE::entityToDto);
    }

}
