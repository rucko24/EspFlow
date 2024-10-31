package com.esp.espflow.service.provider;

import com.esp.espflow.entity.EspDeviceInfo;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.ArgumentsProvider;
import reactor.core.publisher.Flux;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
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
                "2457 (100 %)\n" +
                "2457 (100 %)\n" +
                "Read 2457 bytes at 0x00000000 in 2.7 seconds (7.3 kbit/s)...\n" +
                "Hard resetting via RTS pin...";

        Flux<String> actualLines = Flux.just(actualReadedLines);

        return Stream.of(
                Arguments.of(actualLines, actualReadedLines)
        );
    }
}
