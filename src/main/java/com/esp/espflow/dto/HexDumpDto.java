package com.esp.espflow.dto;

import lombok.Builder;
import lombok.Value;

/**
 * @author rubn
 */
@Value
@Builder
public class HexDumpDto {

    String offset;
    String[] hexBytes; // 16 columns for bytes in hex
    String ascii;

}
