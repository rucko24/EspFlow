package com.esp.espflow.service.respository.impl;

import com.esp.espflow.entity.WizardEspEntity;
import com.esp.espflow.dto.WizardEspDto;
import com.esp.espflow.mappers.WizardEspMapper;
import com.esp.espflow.service.respository.WizardEspRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

/**
 * @author rub'n
 */
@Log4j2
@Service
@RequiredArgsConstructor
public class WizardEspService {

    private final WizardEspRepository wizardEspRepository;

    /**
     * From the initial configuration
     *
     * @param wizardEspDto list
     */
    public void saveAll(List<WizardEspDto> wizardEspDto) {
        Stream.of(wizardEspDto)
                .flatMap(Collection::stream)
                .map(WizardEspMapper.INSTANCE::dtoToEntity)
                .peek(entity -> log.info("Initial load, WizardEspEntity saved  {}", entity))
                .forEach(this.wizardEspRepository::save);
    }

    /**
     * Saves the wizard entity or updates it if it exists
     *
     * @param wizardEspDto
     */
    public void save(WizardEspDto wizardEspDto) {
        var entity = WizardEspMapper.INSTANCE.dtoToEntity(wizardEspDto);
        this.wizardEspRepository.findById(entity.getId())
                .ifPresentOrElse(entityIfPresent -> {
                    entityIfPresent.setWizardEnabled(entity.isWizardEnabled());
                    wizardEspRepository.save(entityIfPresent);
                    log.info("Entity updated {}", entityIfPresent);
                }, () -> {
                    wizardEspRepository.save(entity);
                    log.info("Entity saved {}", entity);
                });
    }

    /**
     * @param name of this Wizard
     * @return A {@link WizardEspEntity}
     */
    @Transactional
    public Optional<WizardEspDto> findByName(String name) {
        return this.wizardEspRepository.findByName(name)
                .map(WizardEspMapper.INSTANCE::entityToDto);
    }

    /**
     * Count the wizards with the isWizardEnabled field to true
     *
     * @return A {boolean}
     */
    public boolean areAllWizardsEnabled() {
        return this.wizardEspRepository.areAllWizardsEnabled() == this.wizardEspRepository.count();
    }

}
