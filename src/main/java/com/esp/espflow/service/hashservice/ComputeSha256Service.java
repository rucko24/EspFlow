package com.esp.espflow.service.hashservice;

import com.esp.espflow.configuration.ComputeDigestAlgorithmConfiguration;
import com.esp.espflow.entity.dto.EsptoolSha256Dto;
import com.esp.espflow.exceptions.CanNotComputeSha256Exception;
import com.esp.espflow.service.respository.impl.EsptoolSha256ServiceImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import org.springframework.util.FileCopyUtils;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;

import static com.esp.espflow.util.EspFlowConstants.CAN_NOT_COMPUTE_SHA_256;

/**
 * Compute sha256 from this input file
 *
 * @author rubn
 */
@Log4j2
@Service
@RequiredArgsConstructor
public class ComputeSha256Service {

    private final ComputeDigestAlgorithmConfiguration computeDigestAlgorithmConfiguration;
    private final EsptoolSha256ServiceImpl esptool256Service;

    /**
     * First dir /home/user/.espflow/1.0.0/esptool/esptool.py
     *
     * @param fileName the input file  /home/user/.espflow/1.0.0/esptool/esptool.py
     * @return A {@link Mono} with computed 256 hash
     */
    public Mono<EsptoolSha256Dto> computeSha256(final String fileName) {
        if(Objects.isNull(fileName)  || fileName.isEmpty()) {
            return Mono.error(new CanNotComputeSha256Exception(CAN_NOT_COMPUTE_SHA_256));
        }
        return Mono.fromCallable(() -> this.startComputeSha256(fileName))
                .subscribeOn(Schedulers.boundedElastic())
                .flatMap(Function.identity())
                .switchIfEmpty(Mono.error(new CanNotComputeSha256Exception(CAN_NOT_COMPUTE_SHA_256)));
    }

    /**
     * @param fileName the input file /home/user/.espflow/1.0.0/esptool/esptool.py
     * @return A {@link Mono} with sha256 computed String
     */
    private Mono<EsptoolSha256Dto> startComputeSha256(final String fileName) {
        final Path path = Path.of(fileName);
        try (var bis = new BufferedInputStream(Files.newInputStream(path))) {
            final byte[] buffer = new byte[FileCopyUtils.BUFFER_SIZE];
            final MessageDigest messageDigest = MessageDigest.getInstance(this.computeDigestAlgorithmConfiguration.getDigestAlgorithm());
            int dataRead = 0;
            while ((dataRead = bis.read(buffer)) != -1) {
                messageDigest.update(buffer, 0, dataRead);
            }
            final byte[] bytesDigest = messageDigest.digest();
            final StringBuilder stringBuffer = new StringBuilder();
            for (final byte b : bytesDigest) {
                stringBuffer.append(
                        Integer.toString((b & 0xFF) + 0x100, 16).substring(1)
                );
            }
            final String processedSha256 = stringBuffer.toString();
            log.info("File: {} sha256: {}", path.getFileName(), processedSha256);
            final String osArch = System.getProperty("os.arch");
            log.info("Current-OsArch: [{}]", osArch);
            final Optional<EsptoolSha256Dto> optionalEsptoolSha256Dto = this.esptool256Service.findBySha256(processedSha256);
            if (optionalEsptoolSha256Dto.isPresent()) {
                final EsptoolSha256Dto esptoolSha256Dto = optionalEsptoolSha256Dto.get();
                log.info("entity osArch: [{}]", esptoolSha256Dto.osArch());
                if (esptoolSha256Dto.osArch().contains(osArch)) {
                    return Mono.just(esptoolSha256Dto);
                }
            }
            return Mono.empty();
        } catch (IOException | NoSuchAlgorithmException e) {
            return Mono.empty();
        }
    }

}
