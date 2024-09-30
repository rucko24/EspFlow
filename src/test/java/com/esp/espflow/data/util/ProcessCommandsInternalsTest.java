package com.esp.espflow.data.util;

import lombok.SneakyThrows;
import org.apache.commons.lang3.ArrayUtils;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import reactor.test.StepVerifier;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.PosixFilePermission;
import java.nio.file.attribute.PosixFilePermissions;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author rubn
 */
@SpringBootTest(classes = {ProcessCommandsInternals.class})
class ProcessCommandsInternalsTest {

    @Autowired
    private ProcessCommandsInternals processCommandsInternals;

    @Test
    @DisplayName("Change port permissions, change the password, and port for test, chmod a+rw works too")
    void changePortPermissionsSuccess() {
        if (GetOsName.getOsName() == GetOsName.LINUX) {
            try {
                final String echo = "echo password | sudo -S chmod 666 /dev/ttyUSB1";
                final String[] commands = ArrayUtils.addAll(GetOsName.shellOsName(), echo);
                final int resultCode = processCommandsInternals.execute(commands).waitFor();
                if (resultCode == 0) {
                    System.out.println("Command executed successfully.");

                    assertThat(resultCode).isZero();
                }
            } catch (IOException | InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    @Test
    @DisplayName("Attempted command execution with some bad parameter, should return 1")
    void changePortPermissionsFailure() {
        if (GetOsName.getOsName() == GetOsName.LINUX) {
            try {
                final String echo = "echo password | sudo -S chod 666 /dev/ttyUSB1";
                final String[] commands = ArrayUtils.addAll(GetOsName.shellOsName(), echo);
                final int resultCode = processCommandsInternals.execute(commands).waitFor();

                assertThat(resultCode).isOne();

            } catch (IOException | InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    @Test
    @DisplayName("Execute 'esptool.py version' on windows, without shell cmd.exe /c")
    @SneakyThrows
    void readEmbebdedExecutableEsptool_4_7_0() {
        if (GetOsName.getOsName() == GetOsName.WINDOWS) {

            final var esptoolExecutable = EsptoolPath.esptoolPath();

            var commands = new String[]{esptoolExecutable, "version"};

            StepVerifier.create(processCommandsInternals.processInputStreamLineByLine(commands)
                            .take(1)
                            .log()
                    )
                    .expectNext("esptool.py v4.7.0")
                    .verifyComplete();
        }

    }

    @Test
    void testAEspToolBundleLinux() {

        // /tmp/esptool-bundle/esptool-linux-amd64/

        var esptoolPath = Path.of(System.getProperty("java.io.tmpdir")
                .concat("/esptool-bundle")
                .concat("/esptool-linux-amd64")
                .concat("/esptool"));

        try {

            if(!(GetOsName.getOsName() == GetOsName.WINDOWS)) {
                Set<PosixFilePermission> permissions = PosixFilePermissions.fromString("rw-------");
                Files.setPosixFilePermissions(esptoolPath, permissions);
            }

            if(Files.isExecutable(esptoolPath)) {
                var commands = new String[]{esptoolPath.toAbsolutePath().toString(), "version"};

                StepVerifier.create(processCommandsInternals.processInputStreamLineByLine(commands)
                                .take(1)
                                .log()
                        )
                        .expectNext("esptool.py v4.7.0")
                        .verifyComplete();

            } else {
                System.err.println("Error al establecer permisos en el executable del esptool");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

}