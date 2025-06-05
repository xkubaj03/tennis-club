package com.inqool.tennisclub.service;

import com.inqool.tennisclub.data.model.CourtEntity;
import com.inqool.tennisclub.data.repository.CourtRepository;
import com.inqool.tennisclub.exceptions.EntityNotFoundException;
import com.inqool.tennisclub.exceptions.NonUniqueFieldException;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class CourtService {

    private final CourtRepository courtRepository;

    @Autowired
    public CourtService(CourtRepository courtRepository) {
        this.courtRepository = courtRepository;
    }

    public CourtEntity create(CourtEntity court) {
        if (courtRepository.findByCourtNumber(court.getCourtNumber()).isPresent()) {
            throw new NonUniqueFieldException("CourtNumber with value " + court.getCourtNumber() + " already exists");
        }
        return courtRepository.save(court);
    }

    @Transactional(readOnly = true)
    public CourtEntity findById(Long id) {
        return courtRepository
                .findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Court with id " + id + " not found"));
    }

    @Transactional(readOnly = true)
    public CourtEntity findByCourtNumber(Integer number) {
        return courtRepository
                .findByCourtNumber(number)
                .orElseThrow(() -> new EntityNotFoundException("Court with number " + number + " not found"));
    }

    @Transactional(readOnly = true)
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
        if (!courtRepository.existsById(id)) {
            throw new EntityNotFoundException("Court with id " + id + " not found");
        }

        courtRepository.deleteById(id);
    }
}
