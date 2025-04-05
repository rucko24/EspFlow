package com.esp.espflow.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Convert;
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
@Table(name = "hex_dump_info")
public class HexDumpEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    //column name other than offset, reserved word in h2
    @Column(name = "offset_value")
    private String offset;

    @Convert(converter = StringArrayConverter.class)
    @Column(name = "hexBytes")
    private String[] hexBytes; // 16 columns for bytes in hex

    @Column(name = "ascii")
    private String ascii;

}
