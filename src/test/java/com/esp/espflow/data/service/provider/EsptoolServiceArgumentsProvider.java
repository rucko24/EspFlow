package com.esp.espflow.data.service.provider;

import com.esp.espflow.data.entity.EspDeviceInfo;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.ArgumentsProvider;
import reactor.core.publisher.Flux;

import java.util.stream.Stream;

public class EsptoolServiceArgumentsProvider implements ArgumentsProvider {

    @Override
    public Stream<? extends Arguments> provideArguments(ExtensionContext extensionContext) throws Exception {

        String portForInputStream = "COM3";

        String portWithFriendlyName = "COM3@Silicon Labs CP210x USB to UART Bridge (COM3)";

        Flux<String> actualLines = Flux.just(
                "Detecting chip type... ESP32-S3",
                "Chip is ESP32-S3 (QFN56) (revision v0.0)",
                "Detected flash size: 8MB");

        var expectedLines = EspDeviceInfo.builder()
                .descriptivePortName("Silicon Labs CP210x USB to UART Bridge (COM3)")
                .detectedFlashSize("8MB")
                .decimal("8388608")
                .hex("800000")
                .chipType("ESP32-S3")
                .chipIs("ESP32-S3 (QFN56) (revision v0.0)")
                .build();

        return Stream.of(
                Arguments.of(portForInputStream, portWithFriendlyName, actualLines, expectedLines)
        );
    }
}
