package com.esp.espflow.service.respository.impl;

import com.esp.espflow.dto.HexDumpDto;
import com.esp.espflow.entity.HexDumpEntity;
import com.esp.espflow.service.respository.HexDumpRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class HexDumpServiceTest {

    @InjectMocks
    private HexDumpService hexDumpService;

    @Mock
    private HexDumpRepository hexDumpRepository;

    @Test
    @DisplayName("Should map the DTO to Entity and save it.")
    void case1() {

        HexDumpDto dto = HexDumpDto.builder()
                .offset("0000:")
                .ascii(".A.")
                .hexBytes(new String[]{"00", "41"})
                .build();

        hexDumpService.save(dto);

        verify(hexDumpRepository).save(argThat(entity ->
                entity.getOffset().equals("0000:")
                        && entity.getAscii().equals(".A.")
                        && entity.getHexBytes().length == 2
        ));

        verifyNoMoreInteractions(hexDumpRepository);

    }

    @Test
    @DisplayName("Should search, convert the Entity Page to DTO List and return it.")
    void case2() {

        String filter = "ABC";
        Pageable pageable = PageRequest.of(0, 10);

        HexDumpEntity entity = HexDumpEntity.builder()
                .offset("0000:")
                .ascii("ABC")
                .build();

        when(hexDumpRepository.findByFilterText(filter, pageable)).thenReturn(new PageImpl<>(List.of(entity)));

        List<HexDumpDto> result = hexDumpService.findByFilterText(filter, pageable);

        assertAll("findByFilterText()",
                () -> assertThat(result).isNotEmpty(),
                () -> assertThat(result).hasSize(1),
                () -> assertThat(result.getFirst()).isNotNull(),
                () -> assertThat(result.getFirst().getOffset()).isEqualTo("0000:"),
                () -> assertThat(result.getFirst().getAscii()).isEqualTo("ABC"));

        verify(hexDumpRepository).findByFilterText(filter, pageable);
        verifyNoMoreInteractions(hexDumpRepository);

    }

    @Test
    @DisplayName("Should delete all rows, without any exception")
    void case3() {

        assertThatCode(() -> hexDumpService.deleteAll()).doesNotThrowAnyException();

        verify(hexDumpRepository).deleteAll();
        verifyNoMoreInteractions(hexDumpRepository);

    }

}