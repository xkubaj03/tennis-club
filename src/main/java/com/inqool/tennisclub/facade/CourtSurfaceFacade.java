package com.inqool.tennisclub.facade;

import com.inqool.tennisclub.api.CourtSurfaceDto;
import com.inqool.tennisclub.api.CreateCourtSurfaceDto;
import com.inqool.tennisclub.data.model.CourtSurfaceEntity;
import com.inqool.tennisclub.mappers.CourtSurfaceMapper;
import com.inqool.tennisclub.service.CourtSurfaceService;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Component;

@Component
public class CourtSurfaceFacade {

    private final CourtSurfaceService courtSurfaceService;
    private final CourtSurfaceMapper courtSurfaceMapper;

    private CourtSurfaceFacade(CourtSurfaceService courtSurfaceService, CourtSurfaceMapper courtSurfaceMapper) {
        this.courtSurfaceService = courtSurfaceService;
        this.courtSurfaceMapper = courtSurfaceMapper;
    }

    public CourtSurfaceDto create(CreateCourtSurfaceDto dto) {
        CourtSurfaceEntity entity = courtSurfaceMapper.toEntity(dto);
        CourtSurfaceEntity saved = courtSurfaceService.create(entity);
        return courtSurfaceMapper.toDto(saved);
    }

    public Optional<CourtSurfaceDto> findById(Long id) {
        return courtSurfaceService.findById(id).map(courtSurfaceMapper::toDto);
    }

    public List<CourtSurfaceDto> findAll() {
        return courtSurfaceMapper.toDtoList(courtSurfaceService.findAll());
    }

    public CourtSurfaceDto update(Long id, CreateCourtSurfaceDto dto) {
        CourtSurfaceEntity CourtSurfaceEntity = courtSurfaceMapper.toEntity(dto);

        CourtSurfaceEntity.setId(id);

        CourtSurfaceEntity saved = courtSurfaceService.update(CourtSurfaceEntity);
        return courtSurfaceMapper.toDto(saved);
    }

    public void deleteById(Long id) {
        courtSurfaceService.deleteById(id);
    }
}
