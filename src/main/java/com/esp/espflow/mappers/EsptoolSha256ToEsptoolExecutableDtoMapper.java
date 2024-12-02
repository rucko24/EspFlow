package com.esp.espflow.mappers;

import com.esp.espflow.entity.dto.EsptoolExecutableDto;
import com.esp.espflow.entity.dto.EsptoolSha256Dto;

import static com.esp.espflow.util.EspFlowConstants.ESPTOOL;

/**
 * @author rub'n
 */
public final class EsptoolSha256ToEsptoolExecutableDtoMapper {

    public static final EsptoolSha256ToEsptoolExecutableDtoMapper INSTANCE = new EsptoolSha256ToEsptoolExecutableDtoMapper();

    private EsptoolSha256ToEsptoolExecutableDtoMapper() {
    }

    public EsptoolExecutableDto esptoolSha256ToEsptoolExecutableDto(final String outputFileName,
                                                                    final EsptoolSha256Dto esptoolSha256Dto) {
        return EsptoolExecutableDto.builder()
                .name(ESPTOOL)
                .esptoolVersion(esptoolSha256Dto.esptoolVersion())
                .absolutePathEsptool(outputFileName)
                .isBundled(true)
                .isSelected(true)
                .sha256(esptoolSha256Dto.sha256())
                .build();
    }
}
