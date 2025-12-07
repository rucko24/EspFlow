package com.esp.espflow.service;

import com.esp.espflow.dto.HexDumpDto;
import com.esp.espflow.service.respository.impl.HexDumpService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.nio.charset.StandardCharsets;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.verifyNoMoreInteractions;

@ExtendWith(MockitoExtension.class)
class HexDumpGeneratorServiceTest {

    @InjectMocks
    private HexDumpGeneratorService hexDumpGeneratorService;

    @Mock
    private HexDumpService hexDumpService;

    @Captor
    private ArgumentCaptor<HexDumpDto> dtoCaptor;

    @Test
    @DisplayName("Should process a short line (less than 16 bytes) and pad it with empty strings")
    void case1() {

        String inputString = "Hello";
        byte[] data = inputString.getBytes(StandardCharsets.UTF_8);

        hexDumpGeneratorService.generateHexDump(data);

        HexDumpDto dto = HexDumpDto.builder()
                .offset("00000000:")
                .ascii("Hello")
                .hexBytes(new String[]{"48", "65", "6C", "6C", "6F","","","","","","","","","","",""})
                .build();

        verify(hexDumpService).save(argThat(entity -> entity.getOffset().equals("00000000:")
                        && entity.getAscii().equals("Hello")
                        && entity.getHexBytes().length == 16
        ));

        assertThat(dto.getOffset()).isEqualTo("00000000:");

        assertThat(dto.getAscii()).isEqualTo("Hello");

        String[] hexBytes = dto.getHexBytes();

        assertThat(hexBytes.length).isEqualTo(16);

        assertAll("'H' -> 48, 'e' -> 65, 'l' -> 6C, 'l' -> 6C, 'o' -> 6F",
                () -> assertThat(hexBytes[0]).isEqualTo("48"),
                () -> assertThat(hexBytes[4]).isEqualTo("6F"),
                () -> assertThat(hexBytes[5]).isEmpty(),
                () -> assertThat(hexBytes[15]).isEmpty());

        verifyNoMoreInteractions(hexDumpService);

    }

    @Test
    @DisplayName("Should process multiple lines and calculate the offset correctly")
    void case2() {

        byte[] data = new byte[20];
        for (int i = 0; i < data.length; i++) {
            data[i] = (byte) i; // 0x00, 0x01 ... 0x13
        }

        hexDumpGeneratorService.generateHexDump(data);

        verify(hexDumpService, times(2)).save(dtoCaptor.capture());
        List<HexDumpDto> capturedDtos = dtoCaptor.getAllValues();

        HexDumpDto firstLine = capturedDtos.getFirst();
        assertEquals("00000000:", firstLine.getOffset());
        assertEquals("00", firstLine.getHexBytes()[0]);
        assertEquals("0F", firstLine.getHexBytes()[15]); // El byte 15 es 0x0F

        HexDumpDto secondLine = capturedDtos.get(1);
        assertEquals("00000010:", secondLine.getOffset(), "El offset debe ser 16 en hex (10)");
        assertEquals("10", secondLine.getHexBytes()[0]); // El byte 16 es 0x10
        assertEquals("13", secondLine.getHexBytes()[3]); // El byte 19 es 0x13
        assertEquals("", secondLine.getHexBytes()[4]);   // Padding

        verifyNoMoreInteractions(hexDumpService);

    }

    @Test
    @DisplayName("Should replace non-printable characters with dots" +
            "0x00 (Null), 0x0A (New Line) are not printable. 0x41 ('A') is printable.")
    void case3() {

        byte[] data = new byte[] { 0x00, 0x41, 0x0A };

        hexDumpGeneratorService.generateHexDump(data);

        verify(hexDumpService).save(dtoCaptor.capture());
        HexDumpDto dto = dtoCaptor.getValue();

        // We hope ".A."
        assertEquals(".A.", dto.getAscii());
        assertEquals("00", dto.getHexBytes()[0]);
        assertEquals("41", dto.getHexBytes()[1]);
        assertEquals("0A", dto.getHexBytes()[2]);

        verifyNoMoreInteractions(hexDumpService);

    }

    @Test
    @DisplayName("Should not fail or call the service if the array is empty.")
    void case4() {
        byte[] data = new byte[0];

        hexDumpGeneratorService.generateHexDump(data);

        verifyNoInteractions(hexDumpService);
    }
}