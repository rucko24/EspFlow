package com.esp.espflow.service.provider;

import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.ArgumentsProvider;
import reactor.core.publisher.Flux;

import java.util.stream.Stream;

public class EsptoolServiceWriteFlashArgumentsProvider implements ArgumentsProvider {

    @Override
    public Stream<? extends Arguments> provideArguments(ExtensionContext extensionContext) throws Exception {

        String[] covertedLinesToStringArray = this.convertStringToStringArray(oneLine());

        Flux<String> actualLines = Flux.fromArray(covertedLinesToStringArray);

        String[] expectedLines = this.convertStringToStringArray(oneLine());

        return Stream.of(
                Arguments.of(actualLines, expectedLines)
        );
    }

    public String[] convertStringToStringArray(String oneString) {
        return Stream.of(oneString)
                .map(line -> line.split("\n"))
                .flatMap(Stream::of)
                .toArray(String[]::new);
    }

    private String oneLine() {
        return "esptool.py v4.7.0\n" +
                "Serial port /dev/ttyUSB1\n" +
                "Connecting....\n" +
                "Detecting chip type... Unsupported detection protocol, switching and trying again...\n" +
                "Detecting chip type... ESP8266\n" +
                "Chip is ESP8266EX\n" +
                "Features: WiFi\n" +
                "Crystal is 26MHz\n" +
                "MAC: 2c:f4:32:10:1d:bf\n" +
                "Uploading stub...\n" +
                "Running stub...\n" +
                "Stub running...\n" +
                "Changing baud rate to 230400\n" +
                "Changed.\n" +
                "Configuring flash size...\n" +
                "Auto-detected Flash size: 1MB\n" +
                "Flash will be erased from 0x00000000 to 0x00041fff...\n" +
                "Flash params set to 0x0220\n" +
                "Compressed 270112 bytes to 198663...\n" +
                "Writing at 0x0001101e... (10 %)\n" +
                "Writing at 0x0005101e... (50 %)\n" +
                "Writing at 0x0008101e... (100 %)\n" +
                "Wrote 270112 bytes (198663 compressed) at 0x00000000 in 17.0 seconds (effective 127.0 kbit/s)...\n" +
                "Hash of data verified.\n" +
                "\n" +
                "Leaving...\n" +
                "Hard resetting via RTS pin...";

    }

}
