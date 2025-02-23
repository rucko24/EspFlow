package com.esp.espflow.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
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
@Table(name = "esptool_executable")
public class EsptoolExecutableEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    @Column(name = "absolute_path_esptool", unique = true)
    private String absolutePathEsptool;

    @Column(name = "is_bundled")
    private boolean isBundled;

    @Column(name = "esptool_version")
    private String esptoolVersion;

    @Column(name = "is_selected")
    private boolean isSelected;

    @Column(name = "sha_256")
    private String sha256;

    @Override
    public String toString() {
        return "{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", absolutePathEsptool='" + absolutePathEsptool + '\'' +
                ", isBundled=" + isBundled +
                ", esptoolVersion='" + esptoolVersion + '\'' +
                ", isSelected=" + isSelected +
                ", sha256='" + sha256 + '\'' +
                '}';
    }

}
