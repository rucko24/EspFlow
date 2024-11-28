package com.esp.espflow.mappers;

import com.esp.espflow.entity.EsptoolExecutableEntity;
import com.esp.espflow.entity.dto.EsptoolExecutableDto;

/**
 * @author rub'n
 */
public final class EsptoolBundleMapper {

    public static final EsptoolBundleMapper INSTANCE = new EsptoolBundleMapper();

    private EsptoolBundleMapper() {
    }

    public EsptoolExecutableEntity dtoToEntity(EsptoolExecutableDto esptoolExecutableDto) {
        return EsptoolExecutableEntity.builder()
                .id(esptoolExecutableDto.id())
                .name(esptoolExecutableDto.name())
                .isBundle(esptoolExecutableDto.isBundle())
                .absolutePathEsptool(esptoolExecutableDto.absolutePathEsptool())
                .esptoolVersion(esptoolExecutableDto.esptoolVersion())
                .isSelected(esptoolExecutableDto.isSelected())
                .build();
    }

    public EsptoolExecutableDto entityToDto(EsptoolExecutableEntity entity) {
        return EsptoolExecutableDto.builder()
                .id(entity.getId())
                .name(entity.getName())
                .isBundle(entity.isBundle())
                .absolutePathEsptool(entity.getAbsolutePathEsptool())
                .esptoolVersion(entity.getEsptoolVersion())
                .isSelected(entity.isSelected())
                .build();
    }
}
