package com.esp.espflow.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

@Data
@Entity
@Table(name = "esptool_bundle")
public class EsptoolBundleEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    @Column(name = "absolute_path_esptool")
    private String absolutePathEsptool;

    @Column(name = "is_in_bundle_mode")
    private boolean isInBundleMode;


}
