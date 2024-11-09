package com.esp.espflow.mappers;

import com.esp.espflow.mappers.provider.ExtractChipIsFromStringMapperFailureProvider;
import com.esp.espflow.mappers.provider.ExtractChipIsFromStringMapperSuccessProvider;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ArgumentsSource;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("When extracting the Chip is like ESP82XX or ESP32 from the text processed in the Xterm, we check for possible " +
        "errors and then send the event correctly in the notification panel.")
class ExtractChipIsFromStringMapperTest {

    @ParameterizedTest
    @ArgumentsSource(ExtractChipIsFromStringMapperSuccessProvider.class)
    @DisplayName("The chip is obtained from the input String")
    void getChipIsFromThisString_Success(String inputLines) {

        final String chipIs = ExtractChipIsFromStringMapper.INSTANCE.getChipIsFromThisString(inputLines);

        assertThat(chipIs).isEqualTo("ESP8266EX");

    }

    @ParameterizedTest
    @ArgumentsSource(ExtractChipIsFromStringMapperFailureProvider.class)
    @DisplayName("When the Chip cannot be parsed, cannot be matched because of lowercase f")
    void attemptToParseTheChip_Failure(final String inputLine) {

        final String chipIs = ExtractChipIsFromStringMapper.INSTANCE.getChipIsFromThisString(inputLine);

        assertThat(chipIs).isEqualTo("This chip cannot be parsed");

    }

    @Test
    @DisplayName("Input String is null, something happens when the text arrives at the Xterm and its text is null")
    void attemptToParseTheChip_Failure2() {

        final String chipIs = ExtractChipIsFromStringMapper.INSTANCE.getChipIsFromThisString(null);

        assertThat(chipIs).isEqualTo("This chip cannot be parsed");

    }


}