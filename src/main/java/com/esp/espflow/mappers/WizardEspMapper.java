package com.esp.espflow.mappers;

import com.esp.espflow.entity.WizardEspEntity;
import com.esp.espflow.entity.dto.WizardEspDto;

/**
 * @author rub'n
 */
public class WizardEspMapper {

    public static final WizardEspMapper INSTANCE = new WizardEspMapper();

    private WizardEspMapper() {}

    public WizardEspEntity dtoToEntity(final WizardEspDto dto) {
        final WizardEspEntity entity = new WizardEspEntity();
        entity.setId(dto.id());
        entity.setName(dto.name());
        entity.setWizardEnabled(dto.isWizardEnabled());
        return entity;
    }

    public WizardEspDto entityToDto(final WizardEspEntity entity) {
        return WizardEspDto.builder()
                .id(entity.getId())
                .name(entity.getName())
                .isWizardEnabled(entity.isWizardEnabled())
                .build();
    }
}
