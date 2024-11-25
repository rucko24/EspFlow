package com.esp.espflow.mappers;

import com.esp.espflow.entity.EsptoolBundleEntity;
import com.esp.espflow.entity.dto.EsptoolBundleDto;

/**
 * @author rub'n
 */
public final class EsptoolBundleMapper {

    public static final EsptoolBundleMapper INSTANCE = new EsptoolBundleMapper();

    private EsptoolBundleMapper() {}

    public EsptoolBundleEntity dtoToEntity(EsptoolBundleDto esptoolBundleDto) {
        final EsptoolBundleEntity entity = new EsptoolBundleEntity();
        entity.setId(esptoolBundleDto.id());
        entity.setName(esptoolBundleDto.name());
        entity.setInBundleMode(esptoolBundleDto.isBundle());
        entity.setAbsolutePathEsptool(esptoolBundleDto.absolutePathEsptool());
        return entity;
    }

    public EsptoolBundleDto entityToDto(EsptoolBundleEntity entity) {
        return EsptoolBundleDto.builder()
                .id(entity.getId())
                .name(entity.getName())
                .isBundle(entity.isInBundleMode())
                .absolutePathEsptool(entity.getAbsolutePathEsptool())
                .build();
    }
}
