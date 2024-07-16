package com.nodemcuui.tool.data.service;

import com.fazecast.jSerialComm.SerialPort;
import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author rubn
 */
@Log4j2
@SpringBootTest(classes = {ComPortService.class, SerialPort.class})
class ComPortServiceIT {

    @Autowired
    private ComPortService comPortService;

    @Test
    @DisplayName("Get Set with all ports, and filter by")
    void getPortsList() {

        assertThat(comPortService.getPortsList())
                .isNullOrEmpty();

    }

    @Test
    @DisplayName("Process all ports, if the ports are not processed internally we return zero")
    void countAllDevices() {
        assertThat(comPortService.countAllDevices())
                .isNotNegative()
                .isGreaterThanOrEqualTo(0);
    }

}