package com.esp.espflow.mappers;

import com.esp.espflow.dto.HexDumpDto;
import com.esp.espflow.entity.HexDumpEntity;

/**
 * @author rubn
 */
public final class HexDumpMapper {

    public static final HexDumpMapper INSTANCE = new HexDumpMapper();

    private HexDumpMapper() {}

    public HexDumpDto entityToDto(final HexDumpEntity entity) {
        return HexDumpDto.builder()
                .hexBytes(entity.getHexBytes())
                .offset(entity.getOffset())
                .ascii(entity.getAscii())
                .build();
    }

    public HexDumpEntity dtoToEntity(final HexDumpDto dto) {
        return HexDumpEntity.builder()
                .hexBytes(dto.getHexBytes())
                .offset(dto.getOffset())
                .ascii(dto.getAscii())
                .build();
    }

}
