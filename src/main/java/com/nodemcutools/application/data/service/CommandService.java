package com.nodemcutools.application.data.service;

import lombok.Getter;
import lombok.SneakyThrows;
import lombok.extern.log4j.Log4j2;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.core.io.buffer.DefaultDataBufferFactory;
import org.springframework.stereotype.Service;
import org.springframework.util.FileCopyUtils;
import reactor.core.publisher.Flux;
import reactor.core.scheduler.Schedulers;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * @author rubn
 */
@Service
@Log4j2
@Getter
public class CommandService {

    public static final String CHARSET = "CP850";
    private final List<Long> listProcess = new CopyOnWriteArrayList<>();

    public Flux<String> esptoolVersion() {
        return this.processInputStream("esptool.py", "version")
                .map((String line) -> {
                    final String esp = line.split(" ")[0];
                    final String numberVersion = line.split("\n")[1];
                    return esp.concat(" v")
                            .concat(numberVersion);
                });
    }

    @SneakyThrows
    public Process execute(String... commands) {
        final Process process = new ProcessBuilder()
                .command(commands)
                .redirectErrorStream(Boolean.TRUE)
                .start();
        //set this processs to pid field
        this.listProcess.add(process.pid());
        return process;
    }

    public void killProcess() {
        log.info("killing pid: {}", listProcess);

        this.execute("kill", listProcess.get(0).toString());
    }

    /**
     * @param commands
     * @return Flux<String>
     */
    public Flux<String> processInputStream(final String... commands) {
        return DataBufferUtils.readInputStream(() -> this.execute(commands)
                                .getInputStream(),
                        new DefaultDataBufferFactory(), FileCopyUtils.BUFFER_SIZE)
                .map((DataBuffer dataBuffer) -> {
                    final byte[] bytes = new byte[dataBuffer.readableByteCount()];
                    dataBuffer.read(bytes);
                    DataBufferUtils.release(dataBuffer);
                    return new String(bytes, StandardCharsets.UTF_8);
                }).subscribeOn(Schedulers.boundedElastic());
    }

}
