package com.inqool.tennisclub.data.repository;

import static org.junit.jupiter.api.Assertions.*;

import com.inqool.tennisclub.data.model.CourtSurfaceEntity;
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

@SpringBootTest
@AutoConfigureTestDatabase
@Transactional
@Rollback
public class CourtSurfaceRepositoryImplTest {

    @Autowired
    private CourtSurfaceRepositoryImpl courtSurfaceRepositoryImpl;

    private CourtSurfaceEntity testSurface;

    @BeforeEach
    void setUp() {
        testSurface = new CourtSurfaceEntity();
        testSurface.setSurfaceName("Clay");
        testSurface.setCostPerMinute(new BigDecimal("10.50"));
        testSurface.setActive(true);
    }

    @Test
    void findAll_returnsListOfActiveSurfaces() {
        List<CourtSurfaceEntity> result = courtSurfaceRepositoryImpl.findAll();

        assertNotNull(result);

        assertTrue(result.stream().allMatch(CourtSurfaceEntity::isActive));
    }

    @Test
    void save_withNull_throwsException() {
        assertThrows(InvalidDataAccessApiUsageException.class, () -> {
            courtSurfaceRepositoryImpl.save(null);
        });
    }

    @Test
    void save_validEntity_returnsSavedEntity() {
        CourtSurfaceEntity saved = courtSurfaceRepositoryImpl.save(testSurface);

        assertNotNull(saved);
        assertNotNull(saved.getId());
        assertEquals("Clay", saved.getSurfaceName());
        assertEquals(new BigDecimal("10.50"), saved.getCostPerMinute());
        assertTrue(saved.isActive());
    }

    @Test
    void findById_existingId_returnsEntity() {
        CourtSurfaceEntity saved = courtSurfaceRepositoryImpl.save(testSurface);

        Optional<CourtSurfaceEntity> found = courtSurfaceRepositoryImpl.findById(saved.getId());

        assertTrue(found.isPresent());
        assertEquals(saved.getId(), found.get().getId());
        assertEquals("Clay", found.get().getSurfaceName());
    }

    @Test
    void findById_nullId_returnsEmpty() {
        Optional<CourtSurfaceEntity> found = courtSurfaceRepositoryImpl.findById(null);
        assertFalse(found.isPresent());
    }

    @Test
    void findById_nonExistingId_returnsEmpty() {
        Optional<CourtSurfaceEntity> found = courtSurfaceRepositoryImpl.findById(999L);
        assertFalse(found.isPresent());
    }

    @Test
    void findBySurfaceName_existingName_returnsEntity() {
        CourtSurfaceEntity saved = courtSurfaceRepositoryImpl.save(testSurface);

        Optional<CourtSurfaceEntity> found = courtSurfaceRepositoryImpl.findBySurfaceName("Clay");

        assertTrue(found.isPresent());
        assertEquals("Clay", found.get().getSurfaceName());
        assertEquals(saved.getId(), found.get().getId());
    }

    @Test
    void findBySurfaceName_nullName_returnsEmpty() {
        Optional<CourtSurfaceEntity> found = courtSurfaceRepositoryImpl.findBySurfaceName(null);
        assertFalse(found.isPresent());
    }

    @Test
    void findBySurfaceName_emptyName_returnsEmpty() {
        Optional<CourtSurfaceEntity> found = courtSurfaceRepositoryImpl.findBySurfaceName("");
        assertFalse(found.isPresent());
    }

    @Test
    void findBySurfaceName_whitespaceOnlyName_returnsEmpty() {
        Optional<CourtSurfaceEntity> found = courtSurfaceRepositoryImpl.findBySurfaceName("   ");
        assertFalse(found.isPresent());
    }

    @Test
    void findBySurfaceName_nonExistingName_returnsEmpty() {
        Optional<CourtSurfaceEntity> found = courtSurfaceRepositoryImpl.findBySurfaceName("NonExisting");
        assertFalse(found.isPresent());
    }

    @Test
    void findAll_withMultipleEntities_returnsAllActiveEntities() {
        CourtSurfaceEntity surface1 = new CourtSurfaceEntity();
        surface1.setSurfaceName("TestClay");
        surface1.setCostPerMinute(new BigDecimal("10.00"));
        surface1.setActive(true);

        CourtSurfaceEntity surface2 = new CourtSurfaceEntity();
        surface2.setSurfaceName("TestGrass");
        surface2.setCostPerMinute(new BigDecimal("15.00"));
        surface2.setActive(true);

        courtSurfaceRepositoryImpl.save(surface1);
        courtSurfaceRepositoryImpl.save(surface2);

        List<CourtSurfaceEntity> all = courtSurfaceRepositoryImpl.findAll();

        assertNotNull(all);

        boolean hasTestClay = all.stream().anyMatch(s -> "TestClay".equals(s.getSurfaceName()));
        boolean hasTestGrass = all.stream().anyMatch(s -> "TestGrass".equals(s.getSurfaceName()));

        assertTrue(hasTestClay, "Should contain TestClay surface");
        assertTrue(hasTestGrass, "Should contain TestGrass surface");

        assertTrue(all.stream().allMatch(CourtSurfaceEntity::isActive), "All returned entities should be active");
    }

    @Test
    void deleteById_existingId_softDeletesEntity() {
        CourtSurfaceEntity saved = courtSurfaceRepositoryImpl.save(testSurface);
        Long id = saved.getId();

        courtSurfaceRepositoryImpl.deleteById(id);

        Optional<CourtSurfaceEntity> found = courtSurfaceRepositoryImpl.findById(id);
        assertFalse(found.isPresent(), "Soft deleted entity should not be returned by findById");

        List<CourtSurfaceEntity> allActive = courtSurfaceRepositoryImpl.findAll();
        boolean foundInActiveList = allActive.stream().anyMatch(entity -> id.equals(entity.getId()));
        assertFalse(foundInActiveList, "Soft deleted entity should not appear in findAll results");
    }

    @Test
    void deleteById_nullId_throwsException() {
        assertThrows(InvalidDataAccessApiUsageException.class, () -> {
            courtSurfaceRepositoryImpl.deleteById(null);
        });
    }

    @Test
    void delete_nullEntity_throwsException() {
        assertThrows(InvalidDataAccessApiUsageException.class, () -> {
            courtSurfaceRepositoryImpl.delete(null);
        });
    }

    @Test
    void existsById_existingId_returnsTrue() {
        CourtSurfaceEntity saved = courtSurfaceRepositoryImpl.save(testSurface);

        boolean exists = courtSurfaceRepositoryImpl.existsById(saved.getId());

        assertTrue(exists);
    }

    @Test
    void existsById_nullId_returnsFalse() {
        boolean exists = courtSurfaceRepositoryImpl.existsById(null);
        assertFalse(exists);
    }

    @Test
    void existsById_nonExistingId_returnsFalse() {
        boolean exists = courtSurfaceRepositoryImpl.existsById(999L);
        assertFalse(exists);
    }

    @Test
    void count_afterSavingEntity_increasesCount() {
        long initialCount = courtSurfaceRepositoryImpl.count();

        courtSurfaceRepositoryImpl.save(testSurface);

        long newCount = courtSurfaceRepositoryImpl.count();
        assertEquals(initialCount + 1, newCount);
    }
}
