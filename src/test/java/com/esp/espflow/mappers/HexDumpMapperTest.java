package com.esp.espflow.mappers;

import com.esp.espflow.entity.HexDumpEntity;
import com.esp.espflow.entity.dto.HexDumpDto;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class HexDumpMapperTest {

    @Test
    @DisplayName("Entity to dto")
    void entityToDto() {

        var entity = HexDumpEntity.builder()
                .hexBytes(new String[]{"dummy-String-bytes"})
                .offset("0000a")
                .ascii("dummy-test")
                .build();

        var dto = HexDumpDto.builder()
                .hexBytes(entity.getHexBytes())
                .offset(entity.getOffset())
                .ascii(entity.getAscii())
                .build();

        assertThat(HexDumpMapper.INSTANCE.entityToDto(entity))
                .usingRecursiveAssertion()
                .isEqualTo(dto);

    }

    @Test
    @DisplayName("Dto to entity")
    void dtoToEntity() {

        var dto = HexDumpDto.builder()
                .hexBytes(new String[]{"dummy-String-bytes"})
                .offset("0000a")
                .ascii("dummy-test")
                .build();

        var entity = HexDumpEntity.builder()
                .hexBytes(dto.getHexBytes())
                .offset(dto.getOffset())
                .ascii(dto.getAscii())
                .build();

        assertThat(HexDumpMapper.INSTANCE.dtoToEntity(dto))
                .usingRecursiveAssertion()
                .isEqualTo(entity);
    }

}