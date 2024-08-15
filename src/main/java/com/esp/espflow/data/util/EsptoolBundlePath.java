package com.esp.espflow.data.util;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static com.esp.espflow.data.util.EspFlowConstants.ESPTOOL_BUNDLE_DIR;

/**
 * @author rubn
 */
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class EsptoolBundlePath {

    /**
     * Use a bundle esptool.py
     *
     * @return A {@link String} with esptool bundle path
     */
    //FIXME move to configuration bean
    public static String esptoolBundlePath() {
        switch (GetOsName.getOsName()) {
            case WINDOWS -> {
                return moveBundleToTempDirectory(ESPTOOL_BUNDLE_DIR + "esptool-winx64/esptool.exe");
            }
            case LINUX -> {
                return moveBundleToTempDirectory(ESPTOOL_BUNDLE_DIR + "esptool-linux-amd64/esptool");
            }
            case MAC -> {
                return "./esptool-bundle/esptool-macOsx64/esptool";
            }
            case FREEBSD -> {
                return "./esptool-bundle/esptool-free bsdx64/esptool";
            }
            default -> {
                return StringUtils.EMPTY;
            }
        }
    }

    public static String moveBundleToTempDirectory(final String fileName) {

        final String tempDir = System.getProperty("java.io.tmpdir").concat("/esptool-bundle-dir/");
        final Path pathTempDir = Path.of(tempDir);

        if (!Files.exists(pathTempDir)) {
            try {
                Files.createDirectory(pathTempDir);
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }

        var esptoolFileNameOutput = Path.of(fileName).getFileName().toString();
        var outPathFileName = Path.of(tempDir + esptoolFileNameOutput);

        try (var inputStream = EsptoolBundlePath.class.getResourceAsStream(fileName);
             var bos = new BufferedOutputStream(Files.newOutputStream(outPathFileName))) {

            inputStream.transferTo(bos);

        } catch (IOException ex) {
            ex.printStackTrace();
        }

        return tempDir + esptoolFileNameOutput;
    }

//    private static void setPermissions() {
//        if (GetOsName.getOsName() == GetOsName.LINUX) {
//            try {
//                final String echo = "echo password | sudo -S chod 666 /tmp/esptool-bundle-dir/esptool";
//                final String[] commands = ArrayUtils.addAll(GetOsName.shellOsName(), echo);
//                final int resultCode = Runtime.getRuntime().exec(commands).waitFor();
//
//                System.out.println("Result code is 1?: " + resultCode);
//
//            } catch (IOException | InterruptedException e) {
//                e.printStackTrace();
//            }
//        }
//    }

}
