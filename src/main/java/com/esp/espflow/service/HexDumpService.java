package com.esp.espflow.service;

import com.esp.espflow.entity.dto.HexDumpDTO;
import org.springframework.stereotype.Service;

import java.util.HexFormat;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

@Service
public class HexDumpService {

    private static final String NON_PRINTABLE_BYTES = "[^\\x20-\\x7E]";

    public List<HexDumpDTO> generateHexDump(byte[] data) {
        final List<HexDumpDTO> lines = new CopyOnWriteArrayList<>();
        final HexFormat hexFormat = HexFormat.of().withUpperCase();

        final int bytesPerLine = 16;
        for (int offset = 0; offset < data.length; offset += bytesPerLine) {

            final int end = Math.min(offset + bytesPerLine, data.length);
            byte[] chunk = new byte[end - offset];
            System.arraycopy(data, offset, chunk, 0, chunk.length);

            // 1. Offset (ej. 00000000)
            final String offsetStr = String.format("%08X", offset).concat(":");

            // 2. Fill hexBytes[0..15], using "" if there are no more bytes
            final String[] hexBytes = new String[bytesPerLine];
            for (int i = 0; i < bytesPerLine; i++) {
                if (i < chunk.length) {
                    // Convert byte to hex with 2 digits
                    hexBytes[i] = hexFormat.toHexDigits(chunk[i]);
                } else {
                    // If the chunk is less than 16 bytes, fill with "" or " ".
                    hexBytes[i] = "";
                }
            }

            // Note: chunk.length could be < 16, but this does not affect the ASCII conversion
            final String ascii = new String(chunk).replaceAll(NON_PRINTABLE_BYTES, ".");

            final HexDumpDTO dto = HexDumpDTO.builder()
                    .offset(offsetStr)
                    .hexBytes(hexBytes)
                    .ascii(ascii)
                    .build();

            lines.add(dto);
        }

        return lines;
    }
}
