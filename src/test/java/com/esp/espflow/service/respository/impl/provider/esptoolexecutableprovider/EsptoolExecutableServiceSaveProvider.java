package com.esp.espflow.service.respository.impl.provider.esptoolexecutableprovider;

import com.esp.espflow.entity.EsptoolExecutableEntity;
import com.esp.espflow.entity.dto.EsptoolExecutableDto;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.ArgumentsProvider;

import java.util.stream.Stream;

public class EsptoolExecutableServiceSaveProvider implements ArgumentsProvider {

    @Override
    public Stream<? extends Arguments> provideArguments(ExtensionContext extensionContext) throws Exception {

        final EsptoolExecutableEntity entityToSave = EsptoolExecutableEntity
                .builder()
                .id(1L)
                .name("esptool")
                .absolutePathEsptool("/tmp/esptool-dir/esptool.py")
                .isBundled(false)
                .esptoolVersion("v4.7.0")
                .isSelected(false)
                .sha256("ae1a3fe6eed5bf7e5dbaee78aea868c5e62f80dd43e13a2f69016da86387a194")
                .build();

        final EsptoolExecutableDto dtoToSave = EsptoolExecutableDto
                .builder()
                .id(1L)
                .name("esptool")
                .absolutePathEsptool("/tmp/esptool-dir/esptool.py")
                .isBundled(false)
                .esptoolVersion("v4.7.0")
                .isSelected(false)
                .sha256("ae1a3fe6eed5bf7e5dbaee78aea868c5e62f80dd43e13a2f69016da86387a194")
                .build();

        return Stream.of(Arguments.of(dtoToSave, entityToSave));
    }

}
