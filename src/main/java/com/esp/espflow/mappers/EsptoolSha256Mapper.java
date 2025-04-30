package com.esp.espflow.mappers;

import com.esp.espflow.entity.EsptoolSha256Entity;
import com.esp.espflow.dto.EsptoolExecutableDto;
import com.esp.espflow.dto.EsptoolSha256Dto;

import static com.esp.espflow.util.EspFlowConstants.ESPTOOL;

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

    public EsptoolExecutableDto esptoolSha256ToEsptoolExecutableDto(final String fileName, final EsptoolSha256Dto esptoolSha256Dto,
                                                                    boolean isBundle, boolean isSelected) {
        return EsptoolExecutableDto.builder()
                .name(ESPTOOL)
                .esptoolVersion("esptool.py ".concat(esptoolSha256Dto.esptoolVersion()))
                .absolutePathEsptool(fileName)
                .isBundled(isBundle)
                .isSelected(isSelected)
                .sha256(esptoolSha256Dto.sha256())
                .build();
    }

}
