package com.esp.espflow.entity.dto;

import lombok.Builder;

/**
 * @author rub'n
 */
@Builder
public record WizardEspDto(boolean isWizardEnabled, String name) {}