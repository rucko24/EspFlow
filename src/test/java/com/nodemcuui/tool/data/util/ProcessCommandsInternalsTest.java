package com.nodemcuui.tool.data.util;

import com.nodemcuui.tool.data.enums.BaudRates;
import org.apache.commons.io.IOUtils;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.util.FileCopyUtils;

import java.io.BufferedInputStream;
import java.io.IOException;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = {ProcessCommandsInternals.class})
class ProcessCommandsInternalsTest {

    @Autowired
    private ProcessCommandsInternals processCommandsInternals;

    @Test
    void execute() {
        String[] commands = new String[]{
                "esptool.py",
                "--port", "/dev/ttyUSB1",
                "--baud", String.valueOf(BaudRates.BAUD_RATE_115200.getBaudRate()),
                "read_flash",
                "0",
                "ALL",
                "backuo.bin"
        };

        try (var inputStream = processCommandsInternals.execute(commands).getInputStream();
             var bufferedInputStream = new BufferedInputStream(inputStream)) {
            final byte[] buffer = new byte[FileCopyUtils.BUFFER_SIZE];
            IOUtils.read(bufferedInputStream, buffer);

        } catch (IOException ex) {
            ex.printStackTrace();
        }

    }
}