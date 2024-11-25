package com.esp.espflow.entity.dto;

import lombok.Builder;

/**
 * @author rubn
 *
 * @param id
 * @param isBundle
 * @param absolutePathEsptool
 */
@Builder
public record EsptoolBundleDto(Long id, String name, boolean isBundle, String absolutePathEsptool) {
}
