package com.esp.espflow.service.respository.impl;

import com.esp.espflow.entity.WizardEspEntity;
import com.esp.espflow.entity.dto.WizardEspDto;
import com.esp.espflow.mappers.WizardEspMapper;
import com.esp.espflow.service.respository.WizardEspRepository;
import com.esp.espflow.service.respository.impl.provider.WizardEspServiceProvider;
import com.esp.espflow.service.respository.impl.provider.WizardUpdateEspServiceProvider;
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
    @ArgumentsSource(WizardEspServiceProvider.class)
    @DisplayName("save items")
    void saveAll(WizardEspDto wizardReadFlashViewDto, WizardEspDto wizardFlashViewDto,
                 WizardEspEntity entityReadViewToReturn, WizardEspEntity entityFlashViewToReturn) {

        WizardEspEntity entityFlash = WizardEspMapper.INSTANCE.dtoToEntity(wizardReadFlashViewDto);
        WizardEspEntity entityRead = WizardEspMapper.INSTANCE.dtoToEntity(wizardFlashViewDto);

        when(repository.save(entityRead)).thenReturn(entityReadViewToReturn);
        when(repository.save(entityFlash)).thenReturn(entityFlashViewToReturn);

        assertThatCode(() -> wizardEspService.saveAll(List.of(wizardFlashViewDto, wizardReadFlashViewDto)))
                .doesNotThrowAnyException();

        verify(repository, times(1)).save(entityReadViewToReturn);
        verify(repository, times(1)).save(entityFlashViewToReturn);
        verifyNoMoreInteractions(repository);
    }

    @ParameterizedTest
    @ArgumentsSource(WizardUpdateEspServiceProvider.class)
    @DisplayName("We update if the entity exists, the id 1L exists, we update the field isWizardEnabled to false")
    void updateEntityWizardReadFlash(WizardEspDto wizardReadFlashViewDto, WizardEspDto updatedDto,
                                     WizardEspEntity updatedEntity) {

        WizardEspEntity toSave = WizardEspMapper.INSTANCE.dtoToEntity(updatedDto);
        WizardEspEntity findById = WizardEspMapper.INSTANCE.dtoToEntity(wizardReadFlashViewDto);

        when(repository.findById(findById.getId())).thenReturn(Optional.of(updatedEntity));
        when(repository.save(toSave)).thenReturn(updatedEntity);

        assertThatCode(() -> wizardEspService.save(updatedDto)).doesNotThrowAnyException();

        verify(repository, times(1)).findById(findById.getId());
        verify(repository, times(1)).save(toSave);
        verifyNoMoreInteractions(repository);
    }

    @ParameterizedTest
    @ArgumentsSource(WizardEspServiceProvider.class)
    @DisplayName("id is empty, the new entity is saved")
    void save(WizardEspDto wizardReadFlashViewDto, WizardEspDto wizardFlashViewDto,
              WizardEspEntity entityReadView, WizardEspEntity entityFlashView) {

        WizardEspEntity toSave = WizardEspMapper.INSTANCE.dtoToEntity(wizardReadFlashViewDto);
        WizardEspEntity toFindBy = WizardEspMapper.INSTANCE.dtoToEntity(wizardReadFlashViewDto);

        when(repository.findById(toFindBy.getId())).thenReturn(Optional.empty());
        when(repository.save(toSave)).thenReturn(entityReadView);

        assertThatCode(() -> wizardEspService.save(wizardReadFlashViewDto)).doesNotThrowAnyException();

        verify(repository, times(1)).findById(toFindBy.getId());
        verify(repository, times(1)).save(entityReadView);
        verifyNoMoreInteractions(repository);
    }

    @ParameterizedTest
    @ArgumentsSource(WizardUpdateEspServiceProvider.class)
    @DisplayName("We save the entity and search by name")
    void findByName(WizardEspDto wizardReadFlashViewDto, WizardEspDto wizardReadFlashViewDtoToSave,
                    WizardEspEntity expectedUpdatedEntity) {

        WizardEspEntity toSave = WizardEspMapper.INSTANCE.dtoToEntity(wizardReadFlashViewDtoToSave);
        WizardEspEntity toFindBy = WizardEspMapper.INSTANCE.dtoToEntity(wizardReadFlashViewDto);

        when(repository.findById(toFindBy.getId())).thenReturn(Optional.of(expectedUpdatedEntity));
        when(repository.save(toSave)).thenReturn(expectedUpdatedEntity);
        when(repository.findByName(WIZARD_READ_FLASH_ESP_VIEW)).thenReturn(Optional.of(expectedUpdatedEntity));

        assertThatCode(() -> wizardEspService.save(wizardReadFlashViewDtoToSave)).doesNotThrowAnyException();

        assertThat(wizardEspService.findByName(WIZARD_READ_FLASH_ESP_VIEW))
                .usingRecursiveComparison()
                .isEqualTo(Optional.of(expectedUpdatedEntity));

        assertAll(() -> {
            wizardEspService.findByName(WIZARD_READ_FLASH_ESP_VIEW)
                    .ifPresent(present -> {
                        assertThat(present.id()).isEqualTo(1);
                        assertThat(present.isWizardEnabled()).isFalse();
                        assertThat(present.name()).contains(WIZARD_READ_FLASH_ESP_VIEW);
                    });
        });

        verify(repository, times(1)).findById(toFindBy.getId());
        verify(repository, times(1)).save(expectedUpdatedEntity);
        verify(repository, times(2)).findByName(WIZARD_READ_FLASH_ESP_VIEW);
        verifyNoMoreInteractions(repository);
    }

    @Test
    @DisplayName("Count how many wizards are true or enabled")
    void areAllWizardsEnabled() {

        when(this.repository.areAllWizardsEnabled()).thenReturn(2);
        when(this.repository.count()).thenReturn(2L);

        assertThat(this.wizardEspService.areAllWizardsEnabled())
                .isTrue();

    }

}