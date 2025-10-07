package com.esp.espflow.service.respository.impl;

import com.esp.espflow.dto.CurrentThemeDto;
import com.esp.espflow.entity.CurrentThemeEntity;
import com.esp.espflow.service.respository.CurrentThemeRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;

/**
 * @author rub'n
 */
@Log4j2
@Service
@RequiredArgsConstructor
public class CurrentThemeService {

    private final CurrentThemeRepository currentThemeRepository;

    public CurrentThemeDto getActiveTheme() {
        return currentThemeRepository.findActiveTheme()
                .map(entity -> CurrentThemeDto.builder()
                        .currentTheme(entity.getCurrentTheme())
                        .build())
                .orElse(null);
    }

    @Transactional
    public void update(CurrentThemeDto dto) {
        // Como solo hay un registro activo (id=1)
        currentThemeRepository.findById(1L)
                .ifPresent(entity -> {
                    var newEntity = CurrentThemeEntity.builder()
                            .id(entity.getId())
                            .isEnabled(entity.getIsEnabled())
                            .currentTheme(dto.getCurrentTheme())
                            .build();
                    currentThemeRepository.save(newEntity);
                    log.info("Entity saved: {}", newEntity);
                });
    }

}
