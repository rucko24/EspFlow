package com.esp.espflow.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 *
 * @author rubn
 *
 * @param success
 * @param message
 * @param chip
 * @param error
 */
public record WebSerialClientDto(

        @JsonProperty("success")
        Boolean success,

        @JsonProperty("message")
        String message,

        @JsonProperty("chip")
        String chip,

        @JsonProperty("error")
        String error) {

}
