package com.esp.espflow.mappers;

import com.esp.espflow.entity.EsptoolExecutableEntity;
import com.esp.espflow.entity.dto.EsptoolExecutableDto;

/**
 * @author rub'n
 */
public final class EsptoolExecutableMapper {

    public static final EsptoolExecutableMapper INSTANCE = new EsptoolExecutableMapper();

    private EsptoolExecutableMapper() {
    }

    public EsptoolExecutableEntity dtoToEntity(EsptoolExecutableDto esptoolExecutableDto) {
        return EsptoolExecutableEntity.builder()
                .id(esptoolExecutableDto.id())
                .name(esptoolExecutableDto.name())
                .isBundled(esptoolExecutableDto.isBundled())
                .absolutePathEsptool(esptoolExecutableDto.absolutePathEsptool())
                .esptoolVersion(esptoolExecutableDto.esptoolVersion())
                .isSelected(esptoolExecutableDto.isSelected())
                .sha256(esptoolExecutableDto.sha256())
                .build();
    }

    public EsptoolExecutableDto entityToDto(EsptoolExecutableEntity entity) {
        return EsptoolExecutableDto.builder()
                .id(entity.getId())
                .name(entity.getName())
                .isBundled(entity.isBundled())
                .absolutePathEsptool(entity.getAbsolutePathEsptool())
                .esptoolVersion(entity.getEsptoolVersion())
                .isSelected(entity.isSelected())
                .sha256(entity.getSha256())
                .build();
    }
}
