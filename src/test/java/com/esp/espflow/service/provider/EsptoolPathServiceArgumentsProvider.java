package com.esp.espflow.service.provider;

import com.esp.espflow.entity.dto.EsptoolExecutableDto;
import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.ArgumentsProvider;

import java.util.Optional;
import java.util.stream.Stream;

/**
 * @author rubn
 */
public class EsptoolPathServiceArgumentsProvider implements ArgumentsProvider {

    @Override
    public Stream<? extends Arguments> provideArguments(ExtensionContext extensionContext) throws Exception {

        // Caso Windows con custom esptool
        final Arguments argumentsWindowsCustom = Arguments.of("Win custom",
                "window",
                Optional.of(EsptoolExecutableDto.builder()
                        .absolutePathEsptool("/tmp/esptool-bundle-dir/esptool-winx64/esptool.exe")
                        .isBundled(false)
                        .build()),
                "/tmp/esptool-bundle-dir/esptool-winx64/esptool.exe"
        );

        // Caso Windows, con bundled = true
        final Arguments argumentsWindowsIsBundle = Arguments.of("Win is bundle",
                "window",
                Optional.of(EsptoolExecutableDto.builder()
                        .absolutePathEsptool("/tmp/esptool-bundle-dir/esptool-winx64/esptool.exe")
                        .isBundled(true)
                        .build()),
                "/tmp/esptool-bundled-dir/esptool-winx64/esptool.exe"
        );

        // Caso Windows (no hay ejecutable seleccionado, se carga el bundle por defecto)
        final Arguments argumentsWindows3Empty = Arguments.of("Win empty loaded default bundle",
                "window",
                Optional.empty(),
                "/tmp/esptool-bundled-dir/esptool-winx64/esptool.exe"
        );

        // Caso Linux con custom esptool
        final Arguments argumentsLinuxCustom = Arguments.of("Linux custom",
                "linux",
                Optional.of(EsptoolExecutableDto.builder()
                        .absolutePathEsptool("/tmp/esptool-custom-dir/esptool-linux-amd64/esptool")
                        .isBundled(false)
                        .build()),
                "/tmp/esptool-custom-dir/esptool-linux-amd64/esptool"
        );

        // Caso Linux, con bundled = true
        final Arguments argumentsLinuxIsBundle = Arguments.of("Linux is bundle",
                "linux",
                Optional.of(EsptoolExecutableDto.builder()
                        .absolutePathEsptool("/tmp/esptool-bundled-dir/esptool-linux-amd64/esptool")
                        .isBundled(true)
                        .build()),
                "/tmp/esptool-bundled-dir/esptool-linux-amd64/esptool"
        );

        // Caso Linux (no hay ejecutable seleccionado, se carga el bundle por defecto)
        final Arguments argumentsLinuxEmpty = Arguments.of("Linux empty loaded default bundle",
                "linux",
                Optional.empty(),
                "/tmp/esptool-bundled-dir/esptool-linux-amd64/esptool"
        );

        // Caso Mac OS: Con esptool.py definido en el DTO
        final Arguments argumentsMacOs = Arguments.of("Mac Os, when absolute path",
                "mac os",
                Optional.of(EsptoolExecutableDto.builder()
                        .absolutePathEsptool("esptool.py")
                        .build()),
                "esptool.py"
        );

        // Caso Mac OS: Sin esptool seleccionado, se usa la ruta por defecto (por ejemplo, si se espera "esptool.py")
        final Arguments argumentsMacOsEmpty = Arguments.of( "Mac os when is empty",
                "mac os",
                Optional.empty(),
                "esptool.py"
        );

        // Caso OS no reconocida: devuelve cadena vacía
        final Arguments argumentsWtfOs = Arguments.of("No os is detected",
                "wtfOs",
                Optional.empty(),
                StringUtils.EMPTY
        );

        return Stream.of(
                argumentsWindowsCustom,
                argumentsWindowsIsBundle,
                argumentsWindows3Empty,

                argumentsLinuxCustom,
                argumentsLinuxIsBundle,
                argumentsLinuxEmpty,

                argumentsMacOs,
                argumentsMacOsEmpty,
                argumentsWtfOs);
    }


}
