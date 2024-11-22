package com.esp.espflow.service.respository.impl.provider;

import com.esp.espflow.entity.WizardEspEntity;
import com.esp.espflow.entity.dto.WizardEspDto;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.ArgumentsProvider;

import java.util.stream.Stream;

import static com.esp.espflow.util.EspFlowConstants.WIZARD_FLASH_ESP_VIEW;
import static com.esp.espflow.util.EspFlowConstants.WIZARD_READ_FLASH_ESP_VIEW;

public class WizardEspServiceProvider implements ArgumentsProvider {

    @Override
    public Stream<? extends Arguments> provideArguments(ExtensionContext extensionContext) throws Exception {

        var wizardReadFlashViewDto = WizardEspDto.builder()
                .id(1L)
                .name(WIZARD_READ_FLASH_ESP_VIEW)
                .isWizardEnabled(true)
                .build();

        var wizardFlashViewDto = WizardEspDto.builder()
                .id(2L)
                .name(WIZARD_FLASH_ESP_VIEW)
                .isWizardEnabled(true)
                .build();

        var entityReadFlashView = new WizardEspEntity();
        entityReadFlashView.setId(1L);
        entityReadFlashView.setWizardEnabled(true);
        entityReadFlashView.setName(WIZARD_READ_FLASH_ESP_VIEW);

        var entityFlashEspView = new WizardEspEntity();
        entityFlashEspView.setId(2L);
        entityFlashEspView.setWizardEnabled(true);
        entityFlashEspView.setName(WIZARD_FLASH_ESP_VIEW);

        return Stream.of(Arguments.of(wizardReadFlashViewDto, wizardFlashViewDto, entityReadFlashView, entityFlashEspView));
    }

}
