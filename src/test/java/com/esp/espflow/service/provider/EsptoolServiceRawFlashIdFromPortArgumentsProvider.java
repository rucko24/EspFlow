package com.esp.espflow.service.provider;

import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.ArgumentsProvider;
import reactor.core.publisher.Flux;

import java.util.stream.Stream;

public class EsptoolServiceRawFlashIdFromPortArgumentsProvider implements ArgumentsProvider {

    @Override
    public Stream<? extends Arguments> provideArguments(ExtensionContext extensionContext) throws Exception {

        Flux<String> actualLines = Flux.just(
                "Detecting chip type... ESP32-S3",
                "Chip is ESP32-S3 (QFN56) (revision v0.0)",
                "Detected flash size: 8MB");

        String expetedFirtsLine = "Detecting chip type... ESP32-S3";
        String expetedSecondLine = "Chip is ESP32-S3 (QFN56) (revision v0.0)";
        String expetedThirdLine = "Detected flash size: 8MB";

        return Stream.of(
                Arguments.of(actualLines,expetedFirtsLine, expetedSecondLine, expetedThirdLine)
        );
    }
}
