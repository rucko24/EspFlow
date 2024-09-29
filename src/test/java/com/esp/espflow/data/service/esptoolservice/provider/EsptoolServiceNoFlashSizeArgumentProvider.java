package com.esp.espflow.data.service.esptoolservice.provider;

import com.esp.espflow.data.entity.EspDeviceInfo;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.ArgumentsProvider;
import reactor.core.publisher.Flux;

import java.util.stream.Stream;

public class EsptoolServiceNoFlashSizeArgumentProvider implements ArgumentsProvider {

    @Override
    public Stream<? extends Arguments> provideArguments(ExtensionContext extensionContext) throws Exception {

        String portForInputStream = "COM3";

        String portWithFriendlyName = "COM3@Silicon Labs CP210x USB to UART Bridge (COM3)";

        Flux<String> actualLines = Flux.just(
                "Detecting chip type... ESP32-S3",
                "Chip is ESP32-S3 (QFN56) (revision v0.0)");

        var expectedLines = EspDeviceInfo.builder()
                .port(portForInputStream)
                .build();

        return Stream.of(
                Arguments.of(portForInputStream, portWithFriendlyName, actualLines, expectedLines)
        );
    }
}
