package com.esp.espflow.service.respository.impl;

import com.esp.espflow.entity.EsptoolExecutableEntity;
import com.esp.espflow.entity.dto.EsptoolExecutableDto;
import com.esp.espflow.service.respository.EsptoolExecutableRepository;
import com.esp.espflow.service.respository.impl.provider.esptoolexecutableprovider.EsptoolExecutableServiceFindByIsSelectedToTrueProvider;
import com.esp.espflow.service.respository.impl.provider.esptoolexecutableprovider.EsptoolExecutableServiceSaveProvider;
import com.esp.espflow.service.respository.impl.provider.esptoolexecutableprovider.EsptoolExecutableServiceUpdateProvider;
import com.esp.espflow.service.respository.impl.provider.esptoolexecutableprovider.EsptoollExecutableServiceFindAllProvider;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ArgumentsSource;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatCode;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

/**
 * @author rubn
 */
@ExtendWith(MockitoExtension.class)
class EsptoolExecutableServiceImplTest {

    @InjectMocks
    private EsptoolExecutableServiceImpl esptoolExecutableService;

    @Mock
    private EsptoolExecutableRepository esptoolExecutableRepository;

    private static final String COMPUTED_SHA_256 = "ae1a3fe6eed5bf7e5dbaee78aea868c5e62f80dd43e13a2f69016da86387a194";

    @ParameterizedTest
    @ArgumentsSource(EsptoolExecutableServiceSaveProvider.class)
    @DisplayName("Save the entity, if esptoolVersion not found and bundled to false")
    void save(EsptoolExecutableDto dtoToSave, EsptoolExecutableEntity entityToSave) {

        when(esptoolExecutableRepository.findByEsptoolVersionWithBundle("v4.7.0", false)).thenReturn(Optional.empty());

        final ArgumentCaptor<EsptoolExecutableEntity> captor = ArgumentCaptor.forClass(EsptoolExecutableEntity.class);
        esptoolExecutableRepository.save(entityToSave);
        verify(esptoolExecutableRepository).save(captor.capture());

        assertThat(this.esptoolExecutableService.save(dtoToSave))
                .usingRecursiveComparison()
                .isEqualTo(captor.getValue());

        verify(esptoolExecutableRepository).save(captor.getValue());

    }

    @ParameterizedTest
    @ArgumentsSource(EsptoolExecutableServiceUpdateProvider.class)
    @DisplayName("Update the entity to isSelected to true, when version and bundled is exist")
    void update(EsptoolExecutableEntity toSave, EsptoolExecutableDto dtoToSave, EsptoolExecutableDto savedDto,
                EsptoolExecutableEntity savedFindByEntity, EsptoolExecutableEntity savedEntity) {

        when(esptoolExecutableRepository.findByEsptoolVersionWithBundle("v4.7.0", false)).thenReturn(Optional.of(savedFindByEntity));

        esptoolExecutableRepository.save(toSave);
        final ArgumentCaptor<EsptoolExecutableEntity> captor = ArgumentCaptor.forClass(EsptoolExecutableEntity.class);
        verify(esptoolExecutableRepository).save(captor.capture());

        assertThat(this.esptoolExecutableService.save(dtoToSave))
                .usingRecursiveComparison()
                .isEqualTo(captor.getValue());

        assertThat(esptoolExecutableService.findByEsptoolVersionWithBundle("v4.7.0", false))
                .map(EsptoolExecutableDto::isSelected)
                .hasValue(true);

        verify(esptoolExecutableRepository, times(2)).findByEsptoolVersionWithBundle("v4.7.0", false);
        verify(esptoolExecutableRepository).save(captor.getValue());

    }

    @Test
    @DisplayName("find by version and bundle to true or false")
    void findByEsptoolVersionWithBundle() {

        final EsptoolExecutableEntity esptoolExecutableEntityToSave = EsptoolExecutableEntity
                .builder()
                .id(1L)
                .name("esptool")
                .absolutePathEsptool("/tmp/esptool-dir/esptool.py")
                .esptoolVersion("v4.7.0")
                .isBundled(false)
                .isSelected(false)
                .sha256(COMPUTED_SHA_256)
                .build();

        when(esptoolExecutableRepository.findByEsptoolVersionWithBundle("v4.7.0", false))
                .thenReturn(Optional.of(esptoolExecutableEntityToSave));

        final Optional<EsptoolExecutableDto> expectedEsptoolExecutableDto = Optional.of(EsptoolExecutableDto
                .builder()
                .id(1L)
                .name("esptool")
                .absolutePathEsptool("/tmp/esptool-dir/esptool.py")
                .esptoolVersion("v4.7.0")
                .isBundled(false)
                .isSelected(false)
                .sha256(COMPUTED_SHA_256)
                .build());

        assertThat(this.esptoolExecutableService.findByEsptoolVersionWithBundle("v4.7.0", false))
                .usingRecursiveComparison()
                .isEqualTo(expectedEsptoolExecutableDto);

        verify(esptoolExecutableRepository).findByEsptoolVersionWithBundle("v4.7.0", false);
        verifyNoMoreInteractions(esptoolExecutableRepository);

    }

    @Test
    @DisplayName("update all items except this id")
    void updateAllSelectedToFalseExcept() {

        assertThatCode(() -> esptoolExecutableService.updateAllSelectedToFalseExcept(1L)).doesNotThrowAnyException();

        verify(esptoolExecutableRepository).updateAllSelectedToFalseExcept(1L);
        verifyNoMoreInteractions(esptoolExecutableRepository);

    }

    @ParameterizedTest
    @ArgumentsSource(EsptoolExecutableServiceFindByIsSelectedToTrueProvider.class)
    @DisplayName("Only is set to true")
    void findByIsSelectedToTrue(EsptoolExecutableEntity entitySaved, EsptoolExecutableDto expectedEsptoolExecutableDto) {

        when(esptoolExecutableRepository.findByIsSelectedToTrue()).thenReturn(Optional.of(entitySaved));

        assertThat(this.esptoolExecutableService.findByIsSelectedToTrue())
                .usingRecursiveComparison()
                .isEqualTo(Optional.of(expectedEsptoolExecutableDto));

        assertThat(this.esptoolExecutableService.findByIsSelectedToTrue())
                .map(EsptoolExecutableDto::isSelected)
                .hasValue(true);


    }

    @Test
    void findByAbsolutePathEsptoolAndIsBundleAndVersion() {
        final EsptoolExecutableEntity esptoolExecutableEntityToFind = EsptoolExecutableEntity
                .builder()
                .id(1L)
                .name("esptool")
                .absolutePathEsptool("/tmp/esptool-dir/esptool.py")
                .esptoolVersion("v4.7.0")
                .isBundled(false)
                .isSelected(true)
                .sha256(COMPUTED_SHA_256)
                .build();

        when(esptoolExecutableRepository.findByAbsolutePathEsptoolAndIsBundleAndVersion(
                esptoolExecutableEntityToFind.getAbsolutePathEsptool(), false,
                esptoolExecutableEntityToFind.getEsptoolVersion())).thenReturn(Optional.of(esptoolExecutableEntityToFind));

        final EsptoolExecutableDto expectedEsptoolExecutableDto = EsptoolExecutableDto
                .builder()
                .id(1L)
                .name("esptool")
                .absolutePathEsptool("/tmp/esptool-dir/esptool.py")
                .esptoolVersion("v4.7.0")
                .isBundled(false)
                .isSelected(true)
                .sha256(COMPUTED_SHA_256)
                .build();

        assertThat(this.esptoolExecutableService.findByAbsolutePathEsptoolAndIsBundleAndVersion(
                expectedEsptoolExecutableDto.absolutePathEsptool(), false, expectedEsptoolExecutableDto.esptoolVersion()))
                .usingRecursiveComparison()
                .isEqualTo(Optional.of(expectedEsptoolExecutableDto));
    }

    @ParameterizedTest
    @ArgumentsSource(EsptoollExecutableServiceFindAllProvider.class)
    @DisplayName("findAll saved two entities")
    void findAll(EsptoolExecutableEntity entity, EsptoolExecutableEntity entity2,
                 EsptoolExecutableDto expectedEsptoolExecutableDto, EsptoolExecutableDto expectedEsptoolExecutableDto2) {

        when(esptoolExecutableRepository.findAll()).thenReturn(List.of(entity, entity2));

        assertThat(this.esptoolExecutableService.findAll())
                .isEqualTo(List.of(expectedEsptoolExecutableDto, expectedEsptoolExecutableDto2))
                .isNotEmpty()
                .isNotNull()
                .hasSize(2);

    }

    @ParameterizedTest
    @ArgumentsSource(EsptoolExecutableServiceUpdateProvider.class)
    @DisplayName("actualiza la entidad con el selected a true y marca todos los id a false menos el actual")
    void selectThisEsptoolExecutableVersion(EsptoolExecutableEntity toSave, EsptoolExecutableDto dtoToSave, EsptoolExecutableDto savedDto,
                                            EsptoolExecutableEntity savedFindByEntity, EsptoolExecutableEntity savedEntity) {

        when(esptoolExecutableRepository.findByEsptoolVersionWithBundle("v4.7.0", false)).thenReturn(Optional.of(savedFindByEntity));

        esptoolExecutableRepository.save(toSave);

        final ArgumentCaptor<EsptoolExecutableEntity> captor = ArgumentCaptor.forClass(EsptoolExecutableEntity.class);

        verify(esptoolExecutableRepository).save(captor.capture());

        assertThatCode(() -> esptoolExecutableService.selectThisEsptoolExecutableVersion(dtoToSave))
                .doesNotThrowAnyException();

    }

}