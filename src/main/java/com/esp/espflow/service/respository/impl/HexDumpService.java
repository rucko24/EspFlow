package com.esp.espflow.service.respository.impl;

import com.esp.espflow.entity.dto.HexDumpDto;
import com.esp.espflow.mappers.HexDumpMapper;
import com.esp.espflow.service.respository.HexDumpRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Log4j2
@Service
@RequiredArgsConstructor
public class HexDumpService {

    private final HexDumpRepository hexDumpRepository;

    public void save(HexDumpDto hexDumpDTO) {
        var entity = HexDumpMapper.INSTANCE.dtoToEntity(hexDumpDTO);
        this.hexDumpRepository.save(entity);
    }

    public Optional<HexDumpDto> findById(final Long id) {
        return this.hexDumpRepository.findById(id)
                .map(HexDumpMapper.INSTANCE::entityToDto);
    }

    public List<HexDumpDto> findAll() {
        return this.hexDumpRepository.findAll()
                .stream()
                .map(HexDumpMapper.INSTANCE::entityToDto)
                .toList();
    }

    public List<HexDumpDto> findByFilterText(String filterText, Pageable pageRequest) {
        return this.hexDumpRepository.findByFilterText(filterText, pageRequest)
                .stream()
                .map(HexDumpMapper.INSTANCE::entityToDto)
                .toList();
    }


    public List<HexDumpDto> findAll(PageRequest pageRequest) {
        return this.hexDumpRepository.findAll(pageRequest)
                .stream()
                .map(HexDumpMapper.INSTANCE::entityToDto)
                .toList();
    }

    public void deleteAll() {
        this.hexDumpRepository.deleteAll();
    }

}
