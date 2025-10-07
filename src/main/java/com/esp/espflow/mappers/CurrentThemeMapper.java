package com.esp.espflow.mappers;

import com.esp.espflow.dto.CurrentThemeDto;
import com.esp.espflow.entity.CurrentThemeEntity;

/**
 * @author rubn
 */
public final class CurrentThemeMapper {

    public static final CurrentThemeMapper INSTANCE = new CurrentThemeMapper();

    private CurrentThemeMapper() {
    }

    public CurrentThemeEntity dtoToEntity(CurrentThemeDto currentThemeDto) {
        return CurrentThemeEntity.builder()
                .currentTheme(currentThemeDto.getCurrentTheme())
                .fontSize(currentThemeDto.getFontSize())
                .isEnabled(currentThemeDto.getIsEnabled())
                .build();
    }

    public CurrentThemeDto entityToDto(CurrentThemeEntity currentThemeEntity) {
        return CurrentThemeDto.builder()
                .id(currentThemeEntity.getId())
                .currentTheme(currentThemeEntity.getCurrentTheme())
                .fontSize(currentThemeEntity.getFontSize())
                .isEnabled(currentThemeEntity.getIsEnabled())
                .build();
    }

}
