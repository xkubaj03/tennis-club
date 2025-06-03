package com.inqool.tennisclub.service;

import com.inqool.tennisclub.api.CourtDto;
import com.inqool.tennisclub.api.CreateCourtDto;
import com.inqool.tennisclub.data.model.CourtEntity;
import com.inqool.tennisclub.data.model.CourtSurfaceEntity;
import com.inqool.tennisclub.data.repository.CourtRepository;
import com.inqool.tennisclub.data.repository.CourtSurfaceRepository;
import com.inqool.tennisclub.exceptions.EntityNotFoundException;
import com.inqool.tennisclub.mappers.CourtMapper;
import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CourtService {

    private final CourtRepository courtRepository;
    private final CourtMapper courtMapper;
    private final CourtSurfaceRepository courtSurfaceRepository;

    @Autowired
    public CourtService(
            CourtRepository courtRepository, CourtMapper courtMapper, CourtSurfaceRepository courtSurfaceRepository) {
        this.courtRepository = courtRepository;
        this.courtMapper = courtMapper;
        this.courtSurfaceRepository = courtSurfaceRepository;
    }

    public CourtEntity create(CourtEntity court) {
        return courtRepository.save(court);
    }

    public CourtDto createCourt(CreateCourtDto dto) {
        CourtSurfaceEntity surface = courtSurfaceRepository
                .findById(dto.getSurfaceId())
                .orElseThrow(() ->
                        new EntityNotFoundException("Court surface with id " + dto.getSurfaceId() + " not found"));

        CourtEntity entity = courtMapper.toEntity(dto);

        entity.setCourtSurface(surface);
        entity.setActive(true);

        CourtEntity saved = courtRepository.save(entity);

        return courtMapper.toDto(saved);
    }

    public CourtEntity createCourtWithParams(Integer courtNumber, Long surfaceId) {
        CourtSurfaceEntity surface = courtSurfaceRepository
                .findById(surfaceId)
                .orElseThrow(() -> new EntityNotFoundException("Court surface with id " + surfaceId + " not found"));

        CourtEntity court = new CourtEntity();
        court.setCourtNumber(courtNumber);
        court.setCourtSurface(surface);
        court.setActive(true);

        return courtRepository.save(court);
    }

    public Optional<CourtEntity> findById(Long id) {
        return Optional.ofNullable(courtRepository
                .findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Court with id " + id + " not found")));
    }

    public List<CourtEntity> findAll() {
        return courtRepository.findAll();
    }

    public CourtEntity update(CourtEntity court) {
        courtRepository
                .findById(court.getId())
                .orElseThrow(() -> new EntityNotFoundException("Court with id " + court.getId() + " not found"));

        return courtRepository.save(court);
    }

    public void deleteById(Long id) {
        courtRepository
                .findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Court with id " + id + " not found"));

        courtRepository.deleteById(id);
    }
}
