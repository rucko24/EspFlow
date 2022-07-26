package com.nodemcutools.application.osname;

import com.nodemcutools.application.data.service.CommandService;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang3.ArrayUtils;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static com.nodemcutools.application.data.util.UiToolConstants.BIN_SH_C;
import static com.nodemcutools.application.data.util.UiToolConstants.CMD_C;
import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author rubn
 */
@Log4j2
@ExtendWith(SpringExtension.class)
class GetOsNameTest {

    @MockBean
    private CommandService commandService;

    @Test
    @DisplayName("Windows")
    void osNameWindows() {
        Mockito.when(commandService.bash()).thenReturn(CMD_C);

        final String[] shellWindow = this.commandService.bash();

        assertThat(shellWindow).isEqualTo(CMD_C);
    }


    @Test
    @DisplayName("Is Linux")
    void osNameLinux() {
        Mockito.when(commandService.bash()).thenReturn(BIN_SH_C);

        final String[] shellLinux = this.commandService.bash();

        assertThat(shellLinux).isEqualTo(BIN_SH_C);
    }

    @Test
    @DisplayName("Is freeBSD")
    void osNameFreeBSD() {
        Mockito.when(commandService.bash()).thenReturn(BIN_SH_C);

        final String[] shellFreeBSD = this.commandService.bash();

        assertThat(shellFreeBSD).isEqualTo(BIN_SH_C);
    }

    @Test
    @DisplayName("Merged commands into one for esptool.py")
    void mergedArrays() {
        Mockito.when(this.commandService.bash()).thenReturn(CMD_C);

        final String[] mergedArray = ArrayUtils.addAll(this.commandService.bash()
                , "esptool.py",
                "version");

        assertThat(mergedArray).isEqualTo(new String[]{"cmd", "/c", "esptool.py", "version"});
    }

}
