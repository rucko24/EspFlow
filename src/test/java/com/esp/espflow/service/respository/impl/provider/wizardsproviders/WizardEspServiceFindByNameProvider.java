package com.esp.espflow.service.respository.impl.provider.wizardsproviders;

import com.esp.espflow.entity.WizardEspEntity;
import com.esp.espflow.entity.dto.WizardEspDto;
import com.esp.espflow.mappers.WizardEspMapper;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.ArgumentsProvider;

import java.util.stream.Stream;

import static com.esp.espflow.util.EspFlowConstants.WIZARD_READ_FLASH_ESP_VIEW;

public class WizardEspServiceFindByNameProvider implements ArgumentsProvider {

    @Override
    public Stream<? extends Arguments> provideArguments(ExtensionContext extensionContext) throws Exception {

        var wizardReadFlashViewDto = WizardEspDto.builder()
                .id(1L)
                .name(WIZARD_READ_FLASH_ESP_VIEW)
                .isWizardEnabled(true)
                .build();

        var wizardReadFlashViewDtoToSave = WizardEspDto.builder()
                .id(1L)
                .name(WIZARD_READ_FLASH_ESP_VIEW)
                .isWizardEnabled(false)
                .build();


        WizardEspEntity toSave = WizardEspMapper.INSTANCE.dtoToEntity(wizardReadFlashViewDtoToSave);
        WizardEspEntity findById = WizardEspMapper.INSTANCE.dtoToEntity(wizardReadFlashViewDto);


        var updatedEntity = new WizardEspEntity();
        updatedEntity.setId(1L);
        updatedEntity.setWizardEnabled(false);
        updatedEntity.setName(WIZARD_READ_FLASH_ESP_VIEW);

        return Stream.of(Arguments.of(toSave, findById, wizardReadFlashViewDtoToSave, updatedEntity));
    }

}
