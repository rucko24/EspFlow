package com.esp.espflow.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
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
@Entity
@Table(name = "current_theme")
public class CurrentThemeEntity {

    @Id
    private Long id;

    @Column(name = "current_theme")
    private String currentTheme;

    @Column(name = "font_size")
    private String fontSize;

    @Column(name = "is_enabled")
    private Boolean isEnabled;

}
