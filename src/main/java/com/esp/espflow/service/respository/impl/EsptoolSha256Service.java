package com.esp.espflow.service.respository.impl;

import com.esp.espflow.dto.EsptoolSha256Dto;
import com.esp.espflow.mappers.EsptoolSha256Mapper;
import com.esp.espflow.service.respository.EsptoolSha256Repository;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;

import java.util.Optional;

/**
 * @author rubn
 */
@Log4j2
@Service
@RequiredArgsConstructor
public class EsptoolSha256Service {

    private final EsptoolSha256Repository esptoolSha256Repository;

    /**
     *
     * @param sha256 to match
     * @return A {@link Optional} with EsptoolSha256Dto
     */
    public Optional<EsptoolSha256Dto> findBySha256(String sha256) {
        return this.esptoolSha256Repository.findBySha256(sha256)
                .map(EsptoolSha256Mapper.INSTANCE::entityToDto);
    }

}
