package com.esp.espflow.entity.dto;

import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class HexDumpDto {

    String offset;
    String[] hexBytes; // 16 columns for bytes in hex
    String ascii;

}
