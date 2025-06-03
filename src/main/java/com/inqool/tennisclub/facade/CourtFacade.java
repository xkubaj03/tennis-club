package com.inqool.tennisclub.facade;

import com.inqool.tennisclub.api.CourtDto;
import com.inqool.tennisclub.api.CreateCourtDto;
import com.inqool.tennisclub.data.model.CourtEntity;
import com.inqool.tennisclub.mappers.CourtMapper;
import com.inqool.tennisclub.service.CourtService;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Component;

@Component
public class CourtFacade {

    private final CourtService courtService;
    private final CourtMapper courtMapper;

    public CourtFacade(CourtService courtService, CourtMapper courtMapper) {
        this.courtService = courtService;
        this.courtMapper = courtMapper;
    }

    public CourtDto create(CreateCourtDto dto) {
        return courtService.createCourt(dto);
    }

    public Optional<CourtDto> findById(Long id) {
        return courtService.findById(id).map(courtMapper::toDto);
    }

    public List<CourtDto> findAll() {
        return courtMapper.toDtoList(courtService.findAll());
    }

    public CourtDto update(Long id, CreateCourtDto dto) {
        CourtEntity entity = courtMapper.toEntity(dto);
        entity.setId(id);
        CourtEntity saved = courtService.update(entity);
        return courtMapper.toDto(saved);
    }

    public void deleteById(Long id) {
        courtService.deleteById(id);
    }
}
