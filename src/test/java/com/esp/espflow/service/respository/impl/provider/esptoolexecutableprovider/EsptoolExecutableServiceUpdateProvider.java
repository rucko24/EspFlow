package com.esp.espflow.service.respository.impl.provider.esptoolexecutableprovider;

import com.esp.espflow.entity.EsptoolExecutableEntity;
import com.esp.espflow.dto.EsptoolExecutableDto;
import com.esp.espflow.mappers.EsptoolExecutableMapper;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.ArgumentsProvider;

import java.util.stream.Stream;

public class EsptoolExecutableServiceUpdateProvider implements ArgumentsProvider {

    @Override
    public Stream<? extends Arguments> provideArguments(ExtensionContext extensionContext) throws Exception {

        final EsptoolExecutableEntity toSave = EsptoolExecutableEntity
                .builder()
                .id(1L)
                .name("esptool")
                .absolutePathEsptool("/tmp/esptool-dir/esptool.py")
                .isBundled(false)
                .esptoolVersion("v4.7.0")
                .isSelected(false)
                .sha256("ae1a3fe6eed5bf7e5dbaee78aea868c5e62f80dd43e13a2f69016da86387a194")
                .build();

        final EsptoolExecutableEntity savedFindByEntity = EsptoolExecutableEntity
                .builder()
                .id(1L)
                .name("esptool")
                .absolutePathEsptool("/tmp/esptool-dir/esptool.py")
                .isBundled(false)
                .esptoolVersion("v4.7.0")
                .isSelected(true)
                .sha256("ae1a3fe6eed5bf7e5dbaee78aea868c5e62f80dd43e13a2f69016da86387a194")
                .build();

        final EsptoolExecutableDto toSaveDto = EsptoolExecutableMapper.INSTANCE.entityToDto(toSave);
        final EsptoolExecutableDto dtoToSave = EsptoolExecutableMapper.INSTANCE.entityToDto(toSave);

        final EsptoolExecutableEntity savedEntity = EsptoolExecutableMapper.INSTANCE.fromEntityPresent(1L, toSave);

        return Stream.of(Arguments.of(toSave, dtoToSave, toSaveDto, savedFindByEntity, savedEntity));
    }

}
