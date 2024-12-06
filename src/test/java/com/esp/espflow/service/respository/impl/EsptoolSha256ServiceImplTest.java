package com.esp.espflow.service.respository.impl;

import com.esp.espflow.entity.EsptoolSha256Entity;
import com.esp.espflow.entity.dto.EsptoolSha256Dto;
import com.esp.espflow.service.respository.EsptoolSha256Repository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

/**
 * @author rubn
 */
@ExtendWith(MockitoExtension.class)
class EsptoolSha256ServiceImplTest {

    @InjectMocks
    private EsptoolSha256ServiceImpl esptoolSha256Service;

    @Mock
    private EsptoolSha256Repository esptoolSha256Repository;

    @Test
    @DisplayName("findBy SHA-256")
    void findBySha256() {

        String sha256 = "ae1a3fe6eed5bf7e5dbaee78aea868c5e62f80dd43e13a2f69016da86387a194";

        EsptoolSha256Entity esptoolSha256Entity = EsptoolSha256Entity
                .builder()
                .id(1L)
                .sha256(sha256)
                .build();

        when(esptoolSha256Repository.findBySha256(sha256)).thenReturn(Optional.of(esptoolSha256Entity));

        EsptoolSha256Dto esptoolSha256Dto = EsptoolSha256Dto
                .builder()
                .id(1L)
                .sha256(sha256)
                .build();

        assertThat(esptoolSha256Service.findBySha256(sha256))
                .usingRecursiveComparison()
                .isEqualTo(Optional.of(esptoolSha256Dto));

        verify(esptoolSha256Repository, times(1)).findBySha256(sha256);
        verifyNoMoreInteractions(esptoolSha256Repository);

    }

}