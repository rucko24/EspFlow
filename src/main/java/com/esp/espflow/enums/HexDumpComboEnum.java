package com.esp.espflow.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum HexDumpComboEnum {

    OFFSET("Filter by offset"),
    ASCII("Filter by ascii");

    final String type;

}
