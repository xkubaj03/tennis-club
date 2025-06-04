package com.inqool.tennisclub.service;

import com.inqool.tennisclub.data.model.CourtEntity;
import com.inqool.tennisclub.data.repository.CourtRepository;
import com.inqool.tennisclub.exceptions.EntityNotFoundException;
import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CourtService {

    private final CourtRepository courtRepository;

    @Autowired
    public CourtService(CourtRepository courtRepository) {
        this.courtRepository = courtRepository;
    }

    public CourtEntity create(CourtEntity court) {
        return courtRepository.save(court);
    }

    public Optional<CourtEntity> findById(Long id) {
        return Optional.ofNullable(courtRepository
                .findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Court with id " + id + " not found")));
    }

    public Optional<CourtEntity> findByCourtNumber(Integer number) {
        return Optional.ofNullable(courtRepository
                .findByCourtNumber(number)
                .orElseThrow(() -> new EntityNotFoundException("Court with number " + number + " not found")));
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
