package com.esp.espflow.service.respository.impl;

import com.esp.espflow.entity.WizardEspEntity;
import com.esp.espflow.entity.dto.WizardEspDto;
import com.esp.espflow.mappers.WizardEspMapper;
import com.esp.espflow.service.respository.WizardEspRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;

import java.util.Optional;

/**
 * @author rub'n
 */
@Log4j2
@Service
@RequiredArgsConstructor
public class WizardEspService {

    private final WizardEspRepository wizardEspRepository;

    /**
     * @param wizardEspDto
     */
    public void save(WizardEspDto wizardEspDto) {
        var entity = WizardEspMapper.INSTANCE.dtoToEntity(wizardEspDto);
        this.wizardEspRepository.findByName(entity.getName())
                .ifPresentOrElse(currentEntity -> {
                    currentEntity.setWizardEnabled(entity.isWizardEnabled());
                    wizardEspRepository.save(currentEntity);
                    log.info("Entity updated {}", currentEntity);
                }, () -> {
                    wizardEspRepository.save(entity);
                    log.info("Entity saved {}", entity);
                });
    }

    /**
     *
     * @param name of this Wizard
     *
     * @return A {@link Optional< WizardEspEntity >}
     */
    @Transactional
    public Optional<WizardEspDto> findWizardFlashEsp(String name) {
        return this.wizardEspRepository.findByName(name)
                .map(WizardEspMapper.INSTANCE::entityToDto);
    }

}
