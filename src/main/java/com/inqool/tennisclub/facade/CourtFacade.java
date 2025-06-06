package com.inqool.tennisclub.facade;

import com.inqool.tennisclub.api.CourtDto;
import com.inqool.tennisclub.api.CreateCourtDto;
import com.inqool.tennisclub.data.model.CourtEntity;
import com.inqool.tennisclub.mappers.CourtMapper;
import com.inqool.tennisclub.service.CourtService;
import com.inqool.tennisclub.service.CourtSurfaceService;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class CourtFacade {

    private final CourtService courtService;
    private final CourtMapper courtMapper;
    private final CourtSurfaceService courtSurfaceService;

    @Autowired
    public CourtFacade(CourtService courtService, CourtMapper courtMapper, CourtSurfaceService courtSurfaceService) {
        this.courtService = courtService;
        this.courtMapper = courtMapper;
        this.courtSurfaceService = courtSurfaceService;
    }

    public CourtDto create(CreateCourtDto dto) {
        CourtEntity entity = courtMapper.toEntity(dto, courtSurfaceService);
        CourtEntity saved = courtService.create(entity);
        return courtMapper.toDto(saved);
    }

    public CourtDto findById(Long id) {
        return courtMapper.toDto(courtService.findById(id));
    }

    public List<CourtDto> findAll() {
        return courtMapper.toDtoList(courtService.findAll());
    }

    public CourtDto update(Long id, CreateCourtDto dto) {
        CourtEntity entity = courtMapper.toEntity(dto, courtSurfaceService);
        entity.setId(id);
        CourtEntity saved = courtService.update(entity);
        return courtMapper.toDto(saved);
    }

    public void deleteById(Long id) {
        courtService.deleteById(id);
    }
}
