package com.esp.espflow.mappers;

import com.esp.espflow.entity.EspDeviceInfo;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static com.esp.espflow.util.EspFlowConstants.CHIP_IS;
import static com.esp.espflow.util.EspFlowConstants.CHIP_TYPE;
import static com.esp.espflow.util.EspFlowConstants.CRYSTAL_IS;
import static com.esp.espflow.util.EspFlowConstants.DETECTED_FLASH_SIZE;
import static com.esp.espflow.util.EspFlowConstants.MAC;
import static com.esp.espflow.util.EspFlowConstants.SERIAL_PORT;
import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author rubn
 */
class EspDeviceInfoMapperTest {


    @Test
    @DisplayName("String parsing alone without the value")
    void key() {

        assertThat(EspDeviceInfoMapper.INSTANCE.key(SERIAL_PORT))
                .isEqualTo("Serial port");

        assertThat(EspDeviceInfoMapper.INSTANCE.key(DETECTED_FLASH_SIZE))
                .isEqualTo("Detected flash size");

        assertThat(EspDeviceInfoMapper.INSTANCE.key(MAC))
                .isEqualTo("MAC");

        assertThat(EspDeviceInfoMapper.INSTANCE.key(CRYSTAL_IS))
                .isEqualTo("Crystal is");

        assertThat(EspDeviceInfoMapper.INSTANCE.key(CHIP_TYPE))
                .isEqualTo("Detecting chip type");

        assertThat(EspDeviceInfoMapper.INSTANCE.key(CHIP_IS))
                .isEqualTo("Chip is");

        assertThat(EspDeviceInfoMapper.INSTANCE.key("n/p"))
                .isEqualTo("Unknown");

    }

    @Test
    @DisplayName("This represents the key and String value of each line read from the console, when executing " +
            "esptool.py flash_id")
    void value() {

        assertThat(EspDeviceInfoMapper.INSTANCE.value("Serial port /dev/ttyUSB0"))
                .isEqualTo("/dev/ttyUSB0");

        assertThat(EspDeviceInfoMapper.INSTANCE.value("Detecting chip type... ESP8266"))
                .isEqualTo("ESP8266");

        assertThat(EspDeviceInfoMapper.INSTANCE.value("Detecting chip type... Unsupported detection protocol"))
                .isEqualTo("Detecting chip type... Unsupported detection protocol");

        assertThat(EspDeviceInfoMapper.INSTANCE.value("MAC: 2c:f4:32:10:1d:bf"))
                .isEqualTo("2c:f4:32:10:1d:bf");

        assertThat(EspDeviceInfoMapper.INSTANCE.value("Detected flash size: 1MB"))
                .isEqualTo("1MB");

        assertThat(EspDeviceInfoMapper.INSTANCE.value("Crystal is 26MHz"))
                .isEqualTo("26MHz");

        assertThat(EspDeviceInfoMapper.INSTANCE.value("Chip is ESP8266EX"))
                .isEqualTo("ESP8266EX");

        assertThat(EspDeviceInfoMapper.INSTANCE.value("can't process this line"))
                .isEqualTo("Unknown");

    }

    @Test
    @DisplayName("The Mono.empty() is processed because there is no port reading, i.e. the detected flash size is null, and descriptivePortName also is null")
    void mapToEspDeviceInfoFailure() {

        final Map<String, String> mapWithLines = new ConcurrentHashMap<>();

        Mono<EspDeviceInfo> espDeviceInfo = EspDeviceInfoMapper.INSTANCE.mapToEspDeviceInfo(mapWithLines, null);

        StepVerifier.create(espDeviceInfo)
                .verifyComplete();

    }

    @Test
    @DisplayName("Process this EspDeviceInfo")
    void mapToEspDeviceInfoSuccess() {

        final Map<String, String> mapWithLines = new ConcurrentHashMap<>();
        mapWithLines.put(SERIAL_PORT, "/dev/ttyUSB0");
        mapWithLines.put(DETECTED_FLASH_SIZE, "2MB");
        mapWithLines.put(MAC, "2c:f4:32:10:1d:bf");
        mapWithLines.put(CRYSTAL_IS, "24MHz");
        mapWithLines.put(CHIP_TYPE, "ESP8266");
        mapWithLines.put(CHIP_IS, "ESP8266EX");

        Mono<EspDeviceInfo> espDeviceInfo = EspDeviceInfoMapper.INSTANCE.mapToEspDeviceInfo(mapWithLines, "/dev/ttyUSB0");

        EspDeviceInfo expectedEspDeviceInfo = EspDeviceInfo.builder()
                .port("/dev/ttyUSB0")
                .descriptivePortName("/dev/ttyUSB0")
                .chipType("ESP8266")
                .chipIs("ESP8266EX")
                .crystalIs("24MHz")
                .macAddress("2c:f4:32:10:1d:bf")
                .decimal("2097152")
                .hex("200000")
                .detectedFlashSize("2MB")
                .build();

        StepVerifier.create(espDeviceInfo)
                .expectNext(expectedEspDeviceInfo)
                .verifyComplete();

    }
}