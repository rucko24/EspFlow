package com.esp.espflow.service;

import com.esp.espflow.service.CommandService;
import com.esp.espflow.util.ProcessCommandsInternals;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

import static org.mockito.Mockito.when;

/**
 * @author rubn
 */
@ExtendWith(MockitoExtension.class)
class CommandServiceTest {

    @InjectMocks
    private CommandService commandService;

    @Mock
    private ProcessCommandsInternals processCommandsInternals;

    @Test
    @DisplayName("This part decodes the databuffer internally and allows it to be read line by line.")
    void processInputStreamLineByLine() {

        final String[] actualCommands = {"/bin/sh", "-c", "ipconfig"};

        var returnedValue = Flux.just("enp5s0: flags=4163<UP,BROADCAST,RUNNING,MULTICAST>  mtu 1500",
                "inet 192.168.1.26  netmask 255.255.255.0  broadcast 192.168.1.255");

        when(processCommandsInternals.processInputStreamLineByLine(actualCommands))
                .thenReturn(returnedValue);

        String firstLine = "enp5s0: flags=4163<UP,BROADCAST,RUNNING,MULTICAST>  mtu 1500";
         String secondLine =  "inet 192.168.1.26  netmask 255.255.255.0  broadcast 192.168.1.255";

        StepVerifier.create(commandService.processInputStreamLineByLine(actualCommands))
                .expectNext(firstLine)
                .expectNext(secondLine)
                .verifyComplete();


    }

    @Test
    @DisplayName("This method is used to download the firmware of the esp, and parse the percentage, if it is windows UTF_8 is used.")
    void processCommandsWithCustomCharset() {

        final String[] actualCommands = {"/bin/sh", "-c", "ping google.com"};

        var returnedValue = Flux.just("PING google.com (172.217.17.14) 56(84) bytes of data.\n",
                "64 bytes from mad07s09-in-f14.1e100.net (172.217.17.14): icmp_seq=1 ttl=117 time=12.4 ms");

        when(processCommandsInternals.processCommandsWithCustomCharset(actualCommands))
                .thenReturn(returnedValue);

        String firstLine = "PING google.com (172.217.17.14) 56(84) bytes of data.\n";
        String secondLine =  "64 bytes from mad07s09-in-f14.1e100.net (172.217.17.14): icmp_seq=1 ttl=117 time=12.4 ms";

        StepVerifier.create(commandService.processCommandsWithCustomCharset(actualCommands))
                .expectNext(firstLine)
                .expectNext(secondLine)
                .verifyComplete();

    }


}