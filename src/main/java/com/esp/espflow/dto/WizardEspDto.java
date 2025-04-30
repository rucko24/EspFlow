package com.esp.espflow.dto;

import lombok.Builder;

/**
 * @author rub'n
 */
@Builder
public record WizardEspDto(Long id, boolean isWizardEnabled, String name) {}
