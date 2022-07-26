package com.nodemcutools.application.readserialport;

import com.nodemcutools.application.data.service.ComPortService;
import com.nodemcutools.application.data.service.CommandService;
import lombok.SneakyThrows;
import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Set;
import java.util.concurrent.CountDownLatch;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.nodemcutools.application.data.util.UiToolConstants.DMESG_TTY;
import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author rubn
 */
@Log4j2
@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = {ComPortService.class, CommandService.class})
class PortSerialTest {

    @Autowired
    private CommandService commandService;

    @MockBean
    private ComPortService comPortService;

    @Test
    @SneakyThrows
    @DisplayName("Read serial port dmesg | grep tty in linux")
    void readSerialPortTtyInLinux() {
        final CountDownLatch count = new CountDownLatch(1);
        commandService.processInputStream(DMESG_TTY)
                .doOnTerminate(count::countDown)
                .map((String line) -> line.split(System.lineSeparator()))
                .map((String[] tmp) -> Stream.of(tmp)
                        .map((String line) -> {
                            if (line.contains("tty")) {
                                return "\n".concat(line)
                                        .replace("\\s+", "");
                            }
                            return "";
                        })
                        .collect(Collectors.joining())
                )
                .subscribe(log::info);
        count.await();
    }

    @Test
    @DisplayName("read serial ports acm0 linux")
    void serialPortIsACM0() {
        final Set<String> set = Set.of("/dev/ttyACM0");
        Mockito.when(comPortService.getPortsList()).thenReturn(set);

        assertThat(comPortService.getPortsList()).hasSizeGreaterThan(0);
    }

    @Test
    @DisplayName("read serial ports tty linux")
    void serialPortIsTty() {
        final String ttyUsb0 = "/dev/ttyUSB1";
        final Set<String> set = Set.of(ttyUsb0);

        Mockito.when(comPortService.getPortsList()).thenReturn(set);

        assertThat(comPortService.getPortsList()).isEqualTo(Set.of("/dev/ttyUSB1"));
    }

    @Test
    @DisplayName("test port COM1 windows")
    void serialPortComWindows() {
        final Set<String> set = Set.of("COM1");

        Mockito.when(comPortService.getPortsList()).thenReturn(set);

        assertThat(comPortService.getPortsList()).isEqualTo(Set.of("COM1"));
    }

    @Test
    @DisplayName("test port  FreeBSD")
    void serialPortFreeBSD() {

        final Set<String> set = Set.of("/dev/ttyU0");
        Mockito.when(comPortService.getPortsList()).thenReturn(set);

        assertThat(comPortService.getPortsList()).isEqualTo(Set.of("/dev/ttyU0"));

        final Set<String> set1 = Set.of("/dev/cuaU0");
        Mockito.when(comPortService.getPortsList()).thenReturn(set1);

        assertThat(comPortService.getPortsList()).isEqualTo(Set.of("/dev/cuaU0"));
    }

    @Test
    @DisplayName("Replace characters in COM PORT windows")
    void replaceDobleBackSlash() {
        final String comportWin = "\\\\.\\COM3";

        final String actual = comportWin.replaceAll("\\\\", "")
                .replaceAll("\\.", "");

        assertThat(actual).isEqualTo("COM3");
    }

}
