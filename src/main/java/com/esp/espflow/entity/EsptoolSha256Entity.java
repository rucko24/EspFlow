package com.esp.espflow.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
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
@Entity(name = "esptool_sha256")
public class EsptoolSha256Entity {

    @Id
    private Long id;

    @Column(name = "os_arch")
    private String osArch;

    @Column(name = "esptool_version")
    private String esptoolVersion;

    @Column(name = "sha_256")
    private String sha256;
}
