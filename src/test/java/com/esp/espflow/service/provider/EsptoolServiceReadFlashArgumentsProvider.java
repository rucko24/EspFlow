package com.esp.espflow.service.provider;

import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.ArgumentsProvider;
import reactor.core.publisher.Flux;

import java.util.stream.Stream;

public class EsptoolServiceReadFlashArgumentsProvider implements ArgumentsProvider {

    @Override
    public Stream<? extends Arguments> provideArguments(ExtensionContext extensionContext) throws Exception {

        String actualReadedLines = "Detecting chip type...\n" +
                "ESP8266\n" +
                "Chip is ESP8266EX\n" +
                "Features: WiFi\n" +
                "Crystal is 26MHz\n" +
                "MAC: 2c:f4:32:10:1d:bf\n" +
                "Uploading stub...\n" +
                "Running stub...\n" +
                "Stub running...\n" +
                "1457 (50 %)\n" +
                "2457 (100 %)\n" +
                "Read 2457 bytes at 0x00000000 in 2.7 seconds (7.3 kbit/s)...\n" +
                "Hard resetting via RTS pin...";

        Flux<String> actualLines = Flux.fromArray(this.convertStringToStringArray(actualReadedLines));

        final String[] expectedLines = this.convertStringToStringArray(actualReadedLines);

        return Stream.of(
                Arguments.of(actualLines, expectedLines)
        );
    }

    private String[] convertStringToStringArray(String one) {
        return Stream.of(one)
                .map(line -> line.split("\n"))
                .flatMap(Stream::of)
                .toArray(String[]::new);
    }
}
