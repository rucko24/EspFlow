package com.nodemcutools.application.esptoolservice;

import com.nodemcutools.application.data.service.ComPortService;
import com.nodemcutools.application.data.service.CommandService;
import com.nodemcutools.application.data.service.EsptoolService;
import lombok.SneakyThrows;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

import static com.nodemcutools.application.data.util.UiToolConstants.FLASH_SIZE;
import static com.nodemcutools.application.data.util.UiToolConstants.MAC;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

/**
 * @author rubn
 */
@Log4j2
@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = {CommandService.class, EsptoolService.class, ComPortService.class})
class EsptoolServiceTest {

    @Autowired
    private CommandService commandService;

    @MockBean
    private EsptoolService esptoolService;

    @Test
    @SneakyThrows
    @DisplayName("esptool.py --port /dev/ttyUSB0 --baud 115200 flash_id")
    void readFlashId() {
        final ConcurrentHashMap<String, String> map = new ConcurrentHashMap<>();
        map.put(FLASH_SIZE, "4MB");
        map.put(MAC, "f4:cf:a2:0f:45:cd");

        Mockito.when(esptoolService.readFlashId()).thenReturn(Flux.just(map));

        StepVerifier.create(this.esptoolService.readFlashId())
                .assertNext(assertMap -> {
                    final String flashSize = assertMap.get(FLASH_SIZE);
                    final String mac = assertMap.get(MAC);

                    final String flash = flashSize.split("MB")[0].trim();
                    final String dec = String.valueOf(Integer.parseInt(flash) * 1048576);
                    final String hex = Integer.toHexString(Integer.parseInt(dec));

                    assertThat(dec).isEqualTo("4194304");
                    assertThat(hex).isEqualTo("400000");
                    assertThat(mac).isEqualTo("f4:cf:a2:0f:45:cd");
                })
                .verifyComplete();

    }

    @Test
    @DisplayName("Flash size, mb, dec, hex")
    void flashSize() {
        String hex = Integer.toHexString(4194304);
        System.out.println("hex: " + "0x".concat(hex));
        System.out.println("MB to decimal/bytes: " + (4 * 1048576));
    }


    @Test
    @DisplayName("Parse flash_id output from esp32")
    @SneakyThrows
    void stringArrayToCollectMap() {

        String s = "Detected flash size: 4MB\nb:12\nMAC: f4:cf:a2:0f:45:cd\n";
        Map<String, String> map = Arrays.stream(s.split(System.lineSeparator()))
                .map(this::mapper)
                .collect(Collectors.toMap(this::keyMapper, this::valueMapper, String::join, LinkedHashMap::new));
        map.entrySet().removeIf(e -> e.getKey().equals(""));

        assertThat(map.get(FLASH_SIZE)).isEqualTo("4MB");
        assertThat(map.get(MAC)).isEqualTo("f4:cf:a2:0f:45:cd");
    }

    private String mapper(final String line) {
        final String tmp = line.split(System.lineSeparator())[0];
        if (tmp.contains(MAC)) {
            return tmp;
        } else if (tmp.contains(FLASH_SIZE)) {
            return tmp;
        }
        return StringUtils.EMPTY;
    }

    private String keyMapper(final String key) {
        if (key.contains(FLASH_SIZE)) {
            return key.split(":")[0].trim();
        } else if (key.contains(MAC)) {
            return key.split(" ")[0].trim().replace(":", "");
        }
        return StringUtils.EMPTY;
    }

    private String valueMapper(final String value) {
        if (value.contains(FLASH_SIZE)) {
            final String s = value.split(":")[1].trim();
            return s;
        } else if (value.contains(MAC)) {
            return value.split(" ")[1].trim();
        }
        return StringUtils.EMPTY;
    }

}
