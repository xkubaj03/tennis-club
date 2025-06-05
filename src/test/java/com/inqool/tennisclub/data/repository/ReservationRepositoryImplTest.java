package com.inqool.tennisclub.data.repository;

import static org.junit.jupiter.api.Assertions.*;

import com.inqool.tennisclub.data.model.*;
import com.inqool.tennisclub.data.model.enums.GameType;
import com.inqool.tennisclub.data.repository.impl.CourtRepositoryImpl;
import com.inqool.tennisclub.data.repository.impl.CourtSurfaceRepositoryImpl;
import com.inqool.tennisclub.data.repository.impl.CustomerRepositoryImpl;
import com.inqool.tennisclub.data.repository.impl.ReservationRepositoryImpl;
import jakarta.transaction.Transactional;
import java.math.BigDecimal;
import java.time.OffsetDateTime;
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
public class ReservationRepositoryImplTest {

    @Autowired
    private ReservationRepositoryImpl reservationRepositoryImpl;

    @Autowired
    private CourtSurfaceRepositoryImpl courtSurfaceRepositoryImpl;

    @Autowired
    private CourtRepositoryImpl courtRepositoryImpl;

    @Autowired
    private CustomerRepositoryImpl customerRepositoryImpl;

    private ReservationEntity testReservation;
    private CourtEntity testCourt;
    private CustomerEntity testCustomer;

    @BeforeEach
    void setUp() {
        CourtSurfaceEntity testSurface = new CourtSurfaceEntity();
        testSurface.setSurfaceName("Clay");
        testSurface.setCostPerMinute(new BigDecimal("10.50"));
        testSurface.setActive(true);
        courtSurfaceRepositoryImpl.save(testSurface);

        testCourt = new CourtEntity();
        testCourt.setCourtNumber(1);
        testCourt.setCourtSurface(testSurface);
        testCourt.setActive(true);
        courtRepositoryImpl.save(testCourt);

        testCustomer = new CustomerEntity();
        testCustomer.setName("John Doe");
        testCustomer.setPhoneNumber("123456789");
        testCustomer.setActive(true);
        customerRepositoryImpl.save(testCustomer);

        testReservation = new ReservationEntity();
        testReservation.setCourt(testCourt);
        testReservation.setGameType(GameType.DOUBLES);
        testReservation.setCustomer(testCustomer);
        testReservation.setStartTime(OffsetDateTime.now().plusHours(1));
        testReservation.setEndTime(OffsetDateTime.now().plusHours(2));
        testReservation.setCreatedAt(OffsetDateTime.now());
        testReservation.setActive(true);
    }

    @Test
    void findAll_returnsListOfActiveReservations() {
        List<ReservationEntity> result = reservationRepositoryImpl.findAll();

        assertNotNull(result);

        assertTrue(result.stream().allMatch(ReservationEntity::isActive));
    }

    @Test
    void save_withNull_throwsException() {
        assertThrows(InvalidDataAccessApiUsageException.class, () -> {
            reservationRepositoryImpl.save(null);
        });
    }

    @Test
    void save_validEntity_returnsSavedEntity() {
        ReservationEntity saved = reservationRepositoryImpl.save(testReservation);

        assertNotNull(saved);
        assertNotNull(saved.getId());
        assertEquals(testCourt, saved.getCourt());
        assertEquals(testCustomer, saved.getCustomer());
        assertTrue(saved.isActive());
    }

    @Test
    void findById_existingId_returnsEntity() {
        ReservationEntity saved = reservationRepositoryImpl.save(testReservation);

        Optional<ReservationEntity> found = reservationRepositoryImpl.findById(saved.getId());

        assertTrue(found.isPresent());
        assertEquals(saved.getId(), found.get().getId());
        assertEquals(testCourt, found.get().getCourt());
    }

    @Test
    void findById_nullId_returnsEmpty() {
        Optional<ReservationEntity> found = reservationRepositoryImpl.findById(null);
        assertFalse(found.isPresent());
    }

    @Test
    void findById_nonExistingId_returnsEmpty() {
        Optional<ReservationEntity> found = reservationRepositoryImpl.findById(999L);
        assertFalse(found.isPresent());
    }

    @Test
    void findByCourtNumberOrderByCreatedAt_existingCourtNumber_returnsReservations() {
        ReservationEntity saved = reservationRepositoryImpl.save(testReservation);

        List<ReservationEntity> found = reservationRepositoryImpl.findByCourtNumberOrderByCreatedAt(1);

        assertNotNull(found);
        assertTrue(found.stream().anyMatch(r -> r.getId().equals(saved.getId())));
        assertTrue(found.stream().allMatch(r -> r.getCourt().getCourtNumber().equals(1)));
    }

    @Test
    void findByCourtNumberOrderByCreatedAt_nullCourtNumber_returnsEmptyList() {
        List<ReservationEntity> found = reservationRepositoryImpl.findByCourtNumberOrderByCreatedAt(null);
        assertNotNull(found);
        assertTrue(found.isEmpty());
    }

    @Test
    void findByCourtNumberOrderByCreatedAt_nonExistingCourtNumber_returnsEmptyList() {
        List<ReservationEntity> found = reservationRepositoryImpl.findByCourtNumberOrderByCreatedAt(999);
        assertNotNull(found);
        assertTrue(found.isEmpty());
    }

    @Test
    void findByCustomerPhoneNumber_existingPhone_returnsReservations() {
        ReservationEntity saved = reservationRepositoryImpl.save(testReservation);

        List<ReservationEntity> found = reservationRepositoryImpl.findByCustomerPhoneNumber("123456789");

        assertNotNull(found);
        assertTrue(found.stream().anyMatch(r -> r.getId().equals(saved.getId())));
        assertTrue(found.stream().allMatch(r -> r.getCustomer().getPhoneNumber().equals("123456789")));
    }

    @Test
    void findByCustomerPhoneNumber_nullPhone_returnsEmptyList() {
        List<ReservationEntity> found = reservationRepositoryImpl.findByCustomerPhoneNumber(null);
        assertNotNull(found);
        assertTrue(found.isEmpty());
    }

    @Test
    void findByCustomerPhoneNumber_emptyPhone_returnsEmptyList() {
        List<ReservationEntity> found = reservationRepositoryImpl.findByCustomerPhoneNumber("");
        assertNotNull(found);
        assertTrue(found.isEmpty());
    }

    @Test
    void findByCustomerPhoneNumber_whitespaceOnlyPhone_returnsEmptyList() {
        List<ReservationEntity> found = reservationRepositoryImpl.findByCustomerPhoneNumber("   ");
        assertNotNull(found);
        assertTrue(found.isEmpty());
    }

    @Test
    void findByCustomerPhoneNumber_phoneWithWhitespace_returnsReservations() {
        ReservationEntity saved = reservationRepositoryImpl.save(testReservation);

        List<ReservationEntity> found = reservationRepositoryImpl.findByCustomerPhoneNumber("  123456789  ");

        assertNotNull(found);
        assertTrue(found.stream().anyMatch(r -> r.getId().equals(saved.getId())));
    }

    @Test
    void findFutureReservationsByCustomerPhoneNumber_existingPhone_returnsFutureReservations() {
        testReservation.setStartTime(OffsetDateTime.now().plusDays(1));
        testReservation.setEndTime(OffsetDateTime.now().plusDays(1).plusHours(1));
        ReservationEntity saved = reservationRepositoryImpl.save(testReservation);

        List<ReservationEntity> found =
                reservationRepositoryImpl.findFutureReservationsByCustomerPhoneNumber("123456789");

        assertNotNull(found);
        assertTrue(found.stream().anyMatch(r -> r.getId().equals(saved.getId())));
        assertTrue(found.stream().allMatch(r -> r.getStartTime().isAfter(OffsetDateTime.now())));
    }

    @Test
    void findFutureReservationsByCustomerPhoneNumber_nullPhone_returnsEmptyList() {
        List<ReservationEntity> found = reservationRepositoryImpl.findFutureReservationsByCustomerPhoneNumber(null);
        assertNotNull(found);
        assertTrue(found.isEmpty());
    }

    @Test
    void findFutureReservationsByCustomerPhoneNumber_emptyPhone_returnsEmptyList() {
        List<ReservationEntity> found = reservationRepositoryImpl.findFutureReservationsByCustomerPhoneNumber("");
        assertNotNull(found);
        assertTrue(found.isEmpty());
    }

    @Test
    void findOverlappingReservations_withOverlap_returnsOverlappingReservations() {
        ReservationEntity saved = reservationRepositoryImpl.save(testReservation);

        OffsetDateTime startTime = testReservation.getStartTime().minusMinutes(30);
        OffsetDateTime endTime = testReservation.getStartTime().plusMinutes(30);

        List<ReservationEntity> found =
                reservationRepositoryImpl.findOverlappingReservations(testCourt.getId(), startTime, endTime);

        assertNotNull(found);
        assertTrue(found.stream().anyMatch(r -> r.getId().equals(saved.getId())));
    }

    @Test
    void findOverlappingReservations_noOverlap_returnsEmptyList() {
        reservationRepositoryImpl.save(testReservation);

        OffsetDateTime startTime = testReservation.getEndTime().plusHours(1);
        OffsetDateTime endTime = testReservation.getEndTime().plusHours(2);

        List<ReservationEntity> found =
                reservationRepositoryImpl.findOverlappingReservations(testCourt.getId(), startTime, endTime);

        assertNotNull(found);
        assertTrue(found.isEmpty());
    }

    @Test
    void findOverlappingReservations_withExclusion_excludesSpecifiedReservation() {
        ReservationEntity saved = reservationRepositoryImpl.save(testReservation);

        OffsetDateTime startTime = testReservation.getStartTime().minusMinutes(30);
        OffsetDateTime endTime = testReservation.getStartTime().plusMinutes(30);

        List<ReservationEntity> found = reservationRepositoryImpl.findOverlappingReservations(
                testCourt.getId(), startTime, endTime, saved.getId());

        assertNotNull(found);
        assertTrue(found.stream().noneMatch(r -> r.getId().equals(saved.getId())));
    }

    @Test
    void findOverlappingReservations_nullCourtId_throwsException() {
        OffsetDateTime startTime = OffsetDateTime.now();
        OffsetDateTime endTime = OffsetDateTime.now().plusHours(1);

        assertThrows(InvalidDataAccessApiUsageException.class, () -> {
            reservationRepositoryImpl.findOverlappingReservations(null, startTime, endTime);
        });
    }

    @Test
    void findOverlappingReservations_nullStartTime_throwsException() {
        OffsetDateTime endTime = OffsetDateTime.now().plusHours(1);

        assertThrows(InvalidDataAccessApiUsageException.class, () -> {
            reservationRepositoryImpl.findOverlappingReservations(1L, null, endTime);
        });
    }

    @Test
    void findOverlappingReservations_nullEndTime_throwsException() {
        OffsetDateTime startTime = OffsetDateTime.now();

        assertThrows(InvalidDataAccessApiUsageException.class, () -> {
            reservationRepositoryImpl.findOverlappingReservations(1L, startTime, null);
        });
    }

    @Test
    void findOverlappingReservations_startTimeAfterEndTime_throwsException() {
        OffsetDateTime startTime = OffsetDateTime.now().plusHours(2);
        OffsetDateTime endTime = OffsetDateTime.now().plusHours(1);

        assertThrows(InvalidDataAccessApiUsageException.class, () -> {
            reservationRepositoryImpl.findOverlappingReservations(1L, startTime, endTime);
        });
    }

    @Test
    void isCourtAvailable_availableSlot_returnsTrue() {
        reservationRepositoryImpl.save(testReservation);

        OffsetDateTime startTime = testReservation.getEndTime().plusHours(1);
        OffsetDateTime endTime = testReservation.getEndTime().plusHours(2);

        boolean available = reservationRepositoryImpl.isCourtAvailable(testCourt.getId(), startTime, endTime);

        assertTrue(available);
    }

    @Test
    void isCourtAvailable_overlappingSlot_returnsFalse() {
        reservationRepositoryImpl.save(testReservation);

        OffsetDateTime startTime = testReservation.getStartTime().minusMinutes(30);
        OffsetDateTime endTime = testReservation.getStartTime().plusMinutes(30);

        boolean available = reservationRepositoryImpl.isCourtAvailable(testCourt.getId(), startTime, endTime);

        assertFalse(available);
    }

    @Test
    void isCourtAvailable_withExclusion_excludesSpecifiedReservation() {
        ReservationEntity saved = reservationRepositoryImpl.save(testReservation);

        OffsetDateTime startTime = testReservation.getStartTime().minusMinutes(30);
        OffsetDateTime endTime = testReservation.getStartTime().plusMinutes(30);

        boolean available =
                reservationRepositoryImpl.isCourtAvailable(testCourt.getId(), startTime, endTime, saved.getId());

        assertTrue(available);
    }

    @Test
    void isCourtAvailable_nullCourtId_throwsException() {
        OffsetDateTime startTime = OffsetDateTime.now();
        OffsetDateTime endTime = OffsetDateTime.now().plusHours(1);

        assertThrows(InvalidDataAccessApiUsageException.class, () -> {
            reservationRepositoryImpl.isCourtAvailable(null, startTime, endTime);
        });
    }

    @Test
    void isCourtAvailable_nullStartTime_throwsException() {
        OffsetDateTime endTime = OffsetDateTime.now().plusHours(1);

        assertThrows(InvalidDataAccessApiUsageException.class, () -> {
            reservationRepositoryImpl.isCourtAvailable(1L, null, endTime);
        });
    }

    @Test
    void isCourtAvailable_nullEndTime_throwsException() {
        OffsetDateTime startTime = OffsetDateTime.now();

        assertThrows(InvalidDataAccessApiUsageException.class, () -> {
            reservationRepositoryImpl.isCourtAvailable(1L, startTime, null);
        });
    }

    @Test
    void isCourtAvailable_startTimeAfterEndTime_throwsException() {
        OffsetDateTime startTime = OffsetDateTime.now().plusHours(2);
        OffsetDateTime endTime = OffsetDateTime.now().plusHours(1);

        assertThrows(InvalidDataAccessApiUsageException.class, () -> {
            reservationRepositoryImpl.isCourtAvailable(1L, startTime, endTime);
        });
    }

    @Test
    void findAll_withMultipleEntities_returnsAllActiveEntities() {
        ReservationEntity reservation1 = new ReservationEntity();
        reservation1.setCourt(testCourt);
        reservation1.setGameType(GameType.DOUBLES);
        reservation1.setCustomer(testCustomer);
        reservation1.setStartTime(OffsetDateTime.now().plusHours(3));
        reservation1.setEndTime(OffsetDateTime.now().plusHours(4));
        reservation1.setCreatedAt(OffsetDateTime.now());
        reservation1.setActive(true);

        ReservationEntity reservation2 = new ReservationEntity();
        reservation2.setCourt(testCourt);
        reservation2.setGameType(GameType.SINGLES);
        reservation2.setCustomer(testCustomer);
        reservation2.setStartTime(OffsetDateTime.now().plusHours(5));
        reservation2.setEndTime(OffsetDateTime.now().plusHours(6));
        reservation2.setCreatedAt(OffsetDateTime.now());
        reservation2.setActive(true);

        reservationRepositoryImpl.save(reservation1);
        reservationRepositoryImpl.save(reservation2);

        List<ReservationEntity> all = reservationRepositoryImpl.findAll();

        assertNotNull(all);
        assertTrue(all.stream().allMatch(ReservationEntity::isActive), "All returned entities should be active");
    }

    @Test
    void deleteById_existingId_softDeletesEntity() {
        ReservationEntity saved = reservationRepositoryImpl.save(testReservation);
        Long id = saved.getId();

        reservationRepositoryImpl.deleteById(id);

        Optional<ReservationEntity> found = reservationRepositoryImpl.findById(id);
        assertFalse(found.isPresent(), "Soft deleted entity should not be returned by findById");

        List<ReservationEntity> allActive = reservationRepositoryImpl.findAll();
        boolean foundInActiveList = allActive.stream().anyMatch(entity -> id.equals(entity.getId()));
        assertFalse(foundInActiveList, "Soft deleted entity should not appear in findAll results");
    }

    @Test
    void deleteById_nullId_throwsException() {
        assertThrows(InvalidDataAccessApiUsageException.class, () -> {
            reservationRepositoryImpl.deleteById(null);
        });
    }

    @Test
    void delete_nullEntity_throwsException() {
        assertThrows(InvalidDataAccessApiUsageException.class, () -> {
            reservationRepositoryImpl.delete(null);
        });
    }

    @Test
    void existsById_existingId_returnsTrue() {
        ReservationEntity saved = reservationRepositoryImpl.save(testReservation);

        boolean exists = reservationRepositoryImpl.existsById(saved.getId());

        assertTrue(exists);
    }

    @Test
    void existsById_nullId_returnsFalse() {
        boolean exists = reservationRepositoryImpl.existsById(null);
        assertFalse(exists);
    }

    @Test
    void existsById_nonExistingId_returnsFalse() {
        boolean exists = reservationRepositoryImpl.existsById(999L);
        assertFalse(exists);
    }

    @Test
    void count_afterSavingEntity_increasesCount() {
        long initialCount = reservationRepositoryImpl.count();

        reservationRepositoryImpl.save(testReservation);

        long newCount = reservationRepositoryImpl.count();
        assertEquals(initialCount + 1, newCount);
    }
}
