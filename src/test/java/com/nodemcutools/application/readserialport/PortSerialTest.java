package com.nodemcutools.application.readserialport;

import com.nodemcutools.application.data.service.ComPortService;
import com.nodemcutools.application.data.service.CommandService;
import lombok.SneakyThrows;
import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.concurrent.CountDownLatch;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.nodemcutools.application.data.util.UiToolConstants.DMESG_TTY;

/**
 * @author rubn
 */
@Log4j2
@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = {ComPortService.class, CommandService.class})
class PortSerialTest {

    @Autowired
    private CommandService commandService;

    @Autowired
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
    @DisplayName("read serial ports with jssc")
    void readSerialPortJssc() {
        comPortService.getPortsList()
                .forEach(log::info);
    }

    @Test
    @DisplayName("Put command to first line")
    void insertCommandToFirstPositionInProcessStream() {
        this.commandService.processInputStream("ifconfig")
                .subscribe((String line) -> {
                   final String rLine = new StringBuilder(line).insert(0 ,"nueva linea \n").toString();
                   log.info("Result {}", rLine);
                });
    }

}
