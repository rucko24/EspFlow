package com.esp.espflow.service;

import com.esp.espflow.entity.dto.HexDumpDTO;
import org.springframework.stereotype.Service;

import java.util.HexFormat;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

@Service
public class HexDumpService {

    public List<HexDumpDTO> generateHexDump(byte[] data) {
        final List<HexDumpDTO> lines = new CopyOnWriteArrayList<>();
        final HexFormat hexFormat = HexFormat.of().withUpperCase();

        final int bytesPerLine = 16;
        for (int offset = 0; offset < data.length; offset += bytesPerLine) {

            final int end = Math.min(offset + bytesPerLine, data.length);
            byte[] chunk = new byte[end - offset];
            System.arraycopy(data, offset, chunk, 0, chunk.length);

            // 1. Offset (ej. 00000000)
            final String offsetStr = String.format("%08X", offset);

            // 2. Rellenar hexBytes[0..15], usando "" si no hay más bytes
            final String[] hexBytes = new String[bytesPerLine];
            for (int i = 0; i < bytesPerLine; i++) {
                if (i < chunk.length) {
                    // Convertir el byte a hex con 2 dígitos
                    String hexValue = hexFormat.toHexDigits(chunk[i]);
                    // hexValue vendrá en minúsculas si no usas withUpperCase(); 
                    // Con withUpperCase() se ve "4A" en vez de "4a"
                    // Asegúrate de ponerlo en mayúsculas si lo deseas
                    hexBytes[i] = hexValue.toUpperCase();
                } else {
                    // Si el chunk es menor a 16 bytes, rellena con "" o "  "
                    hexBytes[i] = "";
                }
            }

            // 3. ASCII (punto para no imprimibles)
            //    Ojo: chunk.length podría ser < 16, pero eso no afecta a la conversión a ASCII
            final String ascii = new String(chunk).replaceAll("[^\\x20-\\x7E]", ".");

            // 4. Crear el DTO
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
