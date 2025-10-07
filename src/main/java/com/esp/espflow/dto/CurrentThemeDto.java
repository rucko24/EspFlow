package com.esp.espflow.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * @author rubn
 */
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CurrentThemeDto {

    private Long id;

    private String currentTheme;

    private String fontSize;

    private Boolean isEnabled;

}
