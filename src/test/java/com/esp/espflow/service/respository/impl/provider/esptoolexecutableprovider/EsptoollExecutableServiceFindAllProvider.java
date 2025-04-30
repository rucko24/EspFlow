package com.esp.espflow.service.respository.impl.provider.esptoolexecutableprovider;

import com.esp.espflow.entity.EsptoolExecutableEntity;
import com.esp.espflow.dto.EsptoolExecutableDto;
import com.esp.espflow.mappers.EsptoolExecutableMapper;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.ArgumentsProvider;

import java.util.stream.Stream;

public class EsptoollExecutableServiceFindAllProvider implements ArgumentsProvider {

    @Override
    public Stream<? extends Arguments> provideArguments(ExtensionContext extensionContext) throws Exception {

        final EsptoolExecutableEntity esptoolExcutableEntity = EsptoolExecutableEntity.builder()
                .id(1L)
                .isBundled(true)
                .esptoolVersion("v4.7.0")
                .isSelected(false)
                .sha256("ae1a3fe6eed5bf7e5dbaee78aea868c5e62f80dd43e13a2f69016da86387a194")
                .build();

        final EsptoolExecutableEntity esptoolExcutableEntity2 = EsptoolExecutableEntity.builder()
                .id(2L)
                .isBundled(false)
                .esptoolVersion("v4.7.0")
                .isSelected(true)
                .sha256("ae1a3fe6eed5bf7e5dbaee78aea868c5e62f80dd43e13a2f69016da86387a194")
                .build();

        final EsptoolExecutableDto expectedDto = EsptoolExecutableMapper.INSTANCE.entityToDto(esptoolExcutableEntity);

        final EsptoolExecutableDto expectedDto2 = EsptoolExecutableMapper.INSTANCE.entityToDto(esptoolExcutableEntity2);

        return Stream.of(Arguments.of(esptoolExcutableEntity, esptoolExcutableEntity2, expectedDto, expectedDto2));
    }

}
