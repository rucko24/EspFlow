package com.esp.espflow.service.respository.impl;

import com.esp.espflow.entity.WizardEspEntity;
import com.esp.espflow.entity.dto.WizardEspDto;
import com.esp.espflow.service.respository.WizardEspRepository;
import com.esp.espflow.service.respository.impl.provider.wizardsproviders.WizardEspServiceFindByNameProvider;
import com.esp.espflow.service.respository.impl.provider.wizardsproviders.WizardEspServiceSaveAllProvider;
import com.esp.espflow.service.respository.impl.provider.wizardsproviders.WizardEspServiceSaveProvider;
import com.esp.espflow.service.respository.impl.provider.wizardsproviders.WizardEspServiceUpdateProvider;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ArgumentsSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static com.esp.espflow.util.EspFlowConstants.WIZARD_READ_FLASH_ESP_VIEW;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatCode;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

/**
 * @author rub'n
 */
@ExtendWith(MockitoExtension.class)
class WizardEspServiceTest {

    @InjectMocks
    private WizardEspService wizardEspService;

    @Mock
    private WizardEspRepository repository;

    @ParameterizedTest
    @ArgumentsSource(WizardEspServiceSaveAllProvider.class)
    @DisplayName("save items")
    void saveAll(WizardEspEntity toSaveReadDto, WizardEspEntity toSaveFlashDto, WizardEspDto wizardReadFlashViewDto,
                 WizardEspDto wizardFlashViewDto,
                 WizardEspEntity entityReadViewToReturn, WizardEspEntity entityFlashViewToReturn) {

        when(repository.save(toSaveFlashDto)).thenReturn(entityReadViewToReturn);
        when(repository.save(toSaveReadDto)).thenReturn(entityFlashViewToReturn);

        assertThatCode(() -> wizardEspService.saveAll(List.of(wizardFlashViewDto, wizardReadFlashViewDto)))
                .doesNotThrowAnyException();

        verify(repository, times(2)).save(entityReadViewToReturn);
        verify(repository, times(2)).save(entityFlashViewToReturn);
        verifyNoMoreInteractions(repository);
    }

    @ParameterizedTest
    @ArgumentsSource(WizardEspServiceUpdateProvider.class)
    @DisplayName("We update if the entity exists, the id 1L exists, we update the field isWizardEnabled to false")
    void updateEntityWizardReadFlash(WizardEspEntity toSave, WizardEspEntity findById, WizardEspDto updatedDto,
                                     WizardEspEntity updatedEntity) {

        when(repository.findById(findById.getId())).thenReturn(Optional.of(updatedEntity));
        when(repository.save(toSave)).thenReturn(updatedEntity);
        when(repository.findByName(WIZARD_READ_FLASH_ESP_VIEW)).thenReturn(Optional.of(updatedEntity));

        assertThatCode(() -> wizardEspService.save(updatedDto)).doesNotThrowAnyException();

        assertAll(() -> {
            assertThat(wizardEspService.findByName(WIZARD_READ_FLASH_ESP_VIEW))
                    .map(WizardEspDto::isWizardEnabled)
                    .hasValue(false);
        });

        verify(repository, times(1)).findById(updatedEntity.getId());
        verify(repository, times(1)).findByName(WIZARD_READ_FLASH_ESP_VIEW);
        verify(repository, times(1)).save(updatedEntity);
        verifyNoMoreInteractions(repository);
    }

    @ParameterizedTest
    @ArgumentsSource(WizardEspServiceSaveProvider.class)
    @DisplayName("id is empty, the new entity is saved")
    void save(WizardEspEntity toFindBy, WizardEspEntity toSave, WizardEspDto wizardReadFlashViewDto,
              WizardEspEntity entitySaved) {

        when(repository.findById(toFindBy.getId())).thenReturn(Optional.empty());
        when(repository.save(toSave)).thenReturn(entitySaved);

        assertThatCode(() -> wizardEspService.save(wizardReadFlashViewDto)).doesNotThrowAnyException();

        verify(repository, times(1)).findById(entitySaved.getId());
        verify(repository, times(1)).save(entitySaved);
        verifyNoMoreInteractions(repository);
    }

    @ParameterizedTest
    @ArgumentsSource(WizardEspServiceFindByNameProvider.class)
    @DisplayName("We save the entity and search by name")
    void findByName(WizardEspEntity toSave, WizardEspEntity toFindBy, WizardEspDto updatedDto,
                    WizardEspEntity updatedEntity) {

        when(repository.findById(toFindBy.getId())).thenReturn(Optional.of(updatedEntity));
        when(repository.save(toSave)).thenReturn(updatedEntity);
        when(repository.findByName(WIZARD_READ_FLASH_ESP_VIEW)).thenReturn(Optional.of(updatedEntity));

        assertThatCode(() -> wizardEspService.save(updatedDto)).doesNotThrowAnyException();

        assertThat(wizardEspService.findByName(WIZARD_READ_FLASH_ESP_VIEW))
                .usingRecursiveComparison()
                .isEqualTo(Optional.of(updatedEntity));

        assertAll(() -> {
            wizardEspService.findByName(WIZARD_READ_FLASH_ESP_VIEW)
                    .ifPresent(present -> {
                        assertThat(present.id()).isEqualTo(1);
                        assertThat(present.isWizardEnabled()).isFalse();
                        assertThat(present.name()).contains(WIZARD_READ_FLASH_ESP_VIEW);
                    });
        });

        verify(repository, times(1)).findById(toFindBy.getId());
        verify(repository, times(1)).save(updatedEntity);
        verify(repository, times(2)).findByName(WIZARD_READ_FLASH_ESP_VIEW);
        verifyNoMoreInteractions(repository);
    }

    @Test
    @DisplayName("We return a boolean to true if each wizard is enabled to true by counting")
    void areAllWizardsEnabled() {

        when(this.repository.areAllWizardsEnabled()).thenReturn(2);
        when(this.repository.count()).thenReturn(2L);

        assertThat(this.wizardEspService.areAllWizardsEnabled())
                .isTrue();

    }

}