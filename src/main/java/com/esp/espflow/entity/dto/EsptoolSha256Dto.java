package com.esp.espflow.entity.dto;

import lombok.Builder;

/**
 * @author rubn
 */
@Builder
public record EsptoolSha256Dto(Long id, String osArch, String esptoolVersion, String sha256) {

}
