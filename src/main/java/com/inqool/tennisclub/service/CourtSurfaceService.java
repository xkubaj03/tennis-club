package com.inqool.tennisclub.service;

import com.inqool.tennisclub.data.model.CourtSurfaceEntity;
import com.inqool.tennisclub.data.repository.CourtSurfaceRepository;
import com.inqool.tennisclub.exceptions.EntityNotFoundException;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class CourtSurfaceService {

    private final CourtSurfaceRepository courtSurfaceRepository;

    @Autowired
    public CourtSurfaceService(CourtSurfaceRepository courtSurfaceRepository) {
        this.courtSurfaceRepository = courtSurfaceRepository;
    }

    public CourtSurfaceEntity create(CourtSurfaceEntity entity) {
        return courtSurfaceRepository.save(entity);
    }

    @Transactional(readOnly = true)
    public CourtSurfaceEntity findById(Long id) {
        return courtSurfaceRepository
                .findById(id)
                .orElseThrow(() -> new EntityNotFoundException("CourtSurface with id " + id + " not found"));
    }

    @Transactional(readOnly = true)
    public List<CourtSurfaceEntity> findAll() {
        return courtSurfaceRepository.findAll();
    }

    public CourtSurfaceEntity update(CourtSurfaceEntity entity) {
        courtSurfaceRepository
                .findById(entity.getId())
                .orElseThrow(
                        () -> new EntityNotFoundException("CourtSurface with id " + entity.getId() + " not found"));

        return courtSurfaceRepository.save(entity);
    }

    public void deleteById(Long id) {
        if (!courtSurfaceRepository.existsById(id)) {
            throw new EntityNotFoundException("CourtSurface with id " + id + " not found");
        }

        courtSurfaceRepository.deleteById(id);
    }
}
