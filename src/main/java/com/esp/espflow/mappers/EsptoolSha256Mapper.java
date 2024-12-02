package com.esp.espflow.mappers;

import com.esp.espflow.entity.EsptoolSha256Entity;
import com.esp.espflow.entity.dto.EsptoolSha256Dto;

/**
 * @author rubn
 */
public final class EsptoolSha256Mapper {

    public static final EsptoolSha256Mapper INSTANCE = new EsptoolSha256Mapper();

    private EsptoolSha256Mapper(){}

    public EsptoolSha256Dto entityToDto(final EsptoolSha256Entity esptoolSha256Entity) {
        return EsptoolSha256Dto.builder()
                .id(esptoolSha256Entity.getId())
                .sha256(esptoolSha256Entity.getSha256())
                .esptoolVersion(esptoolSha256Entity.getEsptoolVersion())
                .osArch(esptoolSha256Entity.getOsArch())
                .build();
    }

    public EsptoolSha256Entity dtoToEntity(final EsptoolSha256Dto esptoolSha256Dto) {
        return EsptoolSha256Entity.builder()
                .id(esptoolSha256Dto.id())
                .sha256(esptoolSha256Dto.sha256())
                .esptoolVersion(esptoolSha256Dto.esptoolVersion())
                .osArch(esptoolSha256Dto.osArch())
                .build();
    }
    
}
