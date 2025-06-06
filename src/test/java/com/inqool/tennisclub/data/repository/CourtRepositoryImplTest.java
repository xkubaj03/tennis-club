package com.inqool.tennisclub.data.repository;

import static org.junit.jupiter.api.Assertions.*;

import com.inqool.tennisclub.data.model.CourtEntity;
import com.inqool.tennisclub.data.model.CourtSurfaceEntity;
import com.inqool.tennisclub.data.repository.impl.CourtRepositoryImpl;
import com.inqool.tennisclub.data.repository.impl.CourtSurfaceRepositoryImpl;
import jakarta.transaction.Transactional;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.InvalidDataAccessApiUsageException;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@AutoConfigureTestDatabase
@Transactional
@Rollback
@ActiveProfiles("test")
public class CourtRepositoryImplTest {

    @Autowired
    private CourtRepositoryImpl courtRepositoryImpl;

    @Autowired
    private CourtSurfaceRepositoryImpl courtSurfaceRepositoryImpl;

    private CourtEntity testCourt;
    private CourtSurfaceEntity testSurface;

    @BeforeEach
    void setUp() {
        testSurface = new CourtSurfaceEntity();
        testSurface.setSurfaceName("Clay");
        testSurface.setCostPerMinute(new BigDecimal("10.50"));
        testSurface.setActive(true);
        courtSurfaceRepositoryImpl.save(testSurface);

        testCourt = new CourtEntity();
        testCourt.setCourtNumber(1);
        testCourt.setCourtSurface(testSurface);
        testCourt.setActive(true);
    }

    @Test
    void findAll_returnsListOfActiveCourts() {
        List<CourtEntity> result = courtRepositoryImpl.findAll();

        assertNotNull(result);

        assertTrue(result.stream().allMatch(CourtEntity::isActive));
    }

    @Test
    void save_withNull_throwsException() {
        assertThrows(InvalidDataAccessApiUsageException.class, () -> {
            courtRepositoryImpl.save(null);
        });
    }

    @Test
    void save_validEntity_returnsSavedEntity() {
        CourtEntity saved = courtRepositoryImpl.save(testCourt);

        assertNotNull(saved);
        assertNotNull(saved.getId());
        assertEquals(1, saved.getCourtNumber());
        assertEquals(testSurface, saved.getCourtSurface());
        assertTrue(saved.isActive());
    }

    @Test
    void findById_existingId_returnsEntity() {
        CourtEntity saved = courtRepositoryImpl.save(testCourt);

        Optional<CourtEntity> found = courtRepositoryImpl.findById(saved.getId());

        assertTrue(found.isPresent());
        assertEquals(saved.getId(), found.get().getId());
        assertEquals(1, found.get().getCourtNumber());
    }

    @Test
    void findById_nullId_returnsEmpty() {
        Optional<CourtEntity> found = courtRepositoryImpl.findById(null);
        assertFalse(found.isPresent());
    }

    @Test
    void findById_nonExistingId_returnsEmpty() {
        Optional<CourtEntity> found = courtRepositoryImpl.findById(999L);
        assertFalse(found.isPresent());
    }

    @Test
    void findByCourtNumber_existingNumber_returnsEntity() {
        CourtEntity saved = courtRepositoryImpl.save(testCourt);

        Optional<CourtEntity> found = courtRepositoryImpl.findByCourtNumber(1);

        assertTrue(found.isPresent());
        assertEquals(1, found.get().getCourtNumber());
        assertEquals(saved.getId(), found.get().getId());
    }

    @Test
    void findByCourtNumber_nullNumber_returnsEmpty() {
        Optional<CourtEntity> found = courtRepositoryImpl.findByCourtNumber(null);
        assertFalse(found.isPresent());
    }

    @Test
    void findByCourtNumber_nonExistingNumber_returnsEmpty() {
        Optional<CourtEntity> found = courtRepositoryImpl.findByCourtNumber(999);
        assertFalse(found.isPresent());
    }

    @Test
    void findByCourtSurface_existingSurface_returnsEntities() {
        CourtEntity saved = courtRepositoryImpl.save(testCourt);

        List<CourtEntity> found = courtRepositoryImpl.findByCourtSurface(testSurface);

        assertNotNull(found);
        assertFalse(found.isEmpty());
        assertTrue(found.stream().anyMatch(c -> c.getId().equals(saved.getId())));
        assertTrue(found.stream().allMatch(c -> c.getCourtSurface().equals(testSurface)));
    }

    @Test
    void findByCourtSurface_nullSurface_returnsEmptyList() {
        List<CourtEntity> found = courtRepositoryImpl.findByCourtSurface(null);
        assertNotNull(found);
        assertTrue(found.isEmpty());
    }

    @Test
    void findAll_withMultipleEntities_returnsAllActiveEntities() {
        CourtEntity court1 = new CourtEntity();
        court1.setCourtNumber(10);
        court1.setCourtSurface(testSurface);
        court1.setActive(true);

        CourtEntity court2 = new CourtEntity();
        court2.setCourtNumber(11);
        court2.setCourtSurface(testSurface);
        court2.setActive(true);

        courtRepositoryImpl.save(court1);
        courtRepositoryImpl.save(court2);

        List<CourtEntity> all = courtRepositoryImpl.findAll();

        assertNotNull(all);

        boolean hasCourt10 = all.stream().anyMatch(c -> c.getCourtNumber().equals(10));
        boolean hasCourt11 = all.stream().anyMatch(c -> c.getCourtNumber().equals(11));

        assertTrue(hasCourt10, "Should contain court 10");
        assertTrue(hasCourt11, "Should contain court 11");

        assertTrue(all.stream().allMatch(CourtEntity::isActive), "All returned entities should be active");
    }

    @Test
    void deleteById_existingId_softDeletesEntity() {
        CourtEntity saved = courtRepositoryImpl.save(testCourt);
        Long id = saved.getId();

        courtRepositoryImpl.deleteById(id);

        Optional<CourtEntity> found = courtRepositoryImpl.findById(id);
        assertFalse(found.isPresent(), "Soft deleted entity should not be returned by findById");

        List<CourtEntity> allActive = courtRepositoryImpl.findAll();
        boolean foundInActiveList = allActive.stream().anyMatch(entity -> id.equals(entity.getId()));
        assertFalse(foundInActiveList, "Soft deleted entity should not appear in findAll results");
    }

    @Test
    void deleteById_nullId_throwsException() {
        assertThrows(InvalidDataAccessApiUsageException.class, () -> {
            courtRepositoryImpl.deleteById(null);
        });
    }

    @Test
    void delete_nullEntity_throwsException() {
        assertThrows(InvalidDataAccessApiUsageException.class, () -> {
            courtRepositoryImpl.delete(null);
        });
    }

    @Test
    void existsById_existingId_returnsTrue() {
        CourtEntity saved = courtRepositoryImpl.save(testCourt);

        boolean exists = courtRepositoryImpl.existsById(saved.getId());

        assertTrue(exists);
    }

    @Test
    void existsById_nullId_returnsFalse() {
        boolean exists = courtRepositoryImpl.existsById(null);
        assertFalse(exists);
    }

    @Test
    void existsById_nonExistingId_returnsFalse() {
        boolean exists = courtRepositoryImpl.existsById(999L);
        assertFalse(exists);
    }

    @Test
    void count_afterSavingEntity_increasesCount() {
        long initialCount = courtRepositoryImpl.count();

        courtRepositoryImpl.save(testCourt);

        long newCount = courtRepositoryImpl.count();
        assertEquals(initialCount + 1, newCount);
    }
}
