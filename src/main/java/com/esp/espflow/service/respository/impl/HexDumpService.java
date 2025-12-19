package com.esp.espflow.service.respository.impl;

import com.esp.espflow.dto.HexDumpDto;
import com.esp.espflow.mappers.HexDumpMapper;
import com.esp.espflow.service.respository.HexDumpRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

/**
 * @author rubn
 */
@Log4j2
@Service
@RequiredArgsConstructor
public class HexDumpService {

    private final HexDumpRepository hexDumpRepository;

    public void save(HexDumpDto hexDumpDTO) {
        var entity = HexDumpMapper.INSTANCE.dtoToEntity(hexDumpDTO);
        this.hexDumpRepository.save(entity);
    }

    public Page<HexDumpDto> findByFilterText(String filterText, Pageable pageRequest) {
        return this.hexDumpRepository.findByFilterText(filterText, pageRequest)
                .map(HexDumpMapper.INSTANCE::entityToDto);
    }

    public void deleteAll() {
        this.hexDumpRepository.deleteAll();
    }

}
