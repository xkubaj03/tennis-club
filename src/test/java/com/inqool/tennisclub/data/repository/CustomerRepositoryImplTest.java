package com.inqool.tennisclub.data.repository;

import static org.junit.jupiter.api.Assertions.*;

import com.inqool.tennisclub.data.model.CustomerEntity;
import com.inqool.tennisclub.data.repository.impl.CustomerRepositoryImpl;
import jakarta.transaction.Transactional;
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
public class CustomerRepositoryImplTest {

    @Autowired
    private CustomerRepositoryImpl customerRepositoryImpl;

    private CustomerEntity testCustomer;

    @BeforeEach
    void setUp() {
        testCustomer = new CustomerEntity();
        testCustomer.setFirstName("John");
        testCustomer.setLastName("Doe");
        testCustomer.setPhoneNumber("123456789");
        testCustomer.setActive(true);
    }

    @Test
    void findAll_returnsListOfActiveCustomers() {
        List<CustomerEntity> result = customerRepositoryImpl.findAll();

        assertNotNull(result);

        assertTrue(result.stream().allMatch(CustomerEntity::isActive));
    }

    @Test
    void save_withNull_throwsException() {
        assertThrows(InvalidDataAccessApiUsageException.class, () -> {
            customerRepositoryImpl.save(null);
        });
    }

    @Test
    void save_validEntity_returnsSavedEntity() {
        CustomerEntity saved = customerRepositoryImpl.save(testCustomer);

        assertNotNull(saved);
        assertNotNull(saved.getId());
        assertEquals("John", saved.getFirstName());
        assertEquals("Doe", saved.getLastName());
        assertEquals("123456789", saved.getPhoneNumber());
        assertTrue(saved.isActive());
    }

    @Test
    void findById_existingId_returnsEntity() {
        CustomerEntity saved = customerRepositoryImpl.save(testCustomer);

        Optional<CustomerEntity> found = customerRepositoryImpl.findById(saved.getId());

        assertTrue(found.isPresent());
        assertEquals(saved.getId(), found.get().getId());
        assertEquals("123456789", found.get().getPhoneNumber());
        assertTrue(found.get().isActive());
        assertEquals("John", found.get().getFirstName());
        assertEquals("Doe", found.get().getLastName());
    }

    @Test
    void findById_nullId_returnsEmpty() {
        Optional<CustomerEntity> found = customerRepositoryImpl.findById(null);
        assertFalse(found.isPresent());
    }

    @Test
    void findById_nonExistingId_returnsEmpty() {
        Optional<CustomerEntity> found = customerRepositoryImpl.findById(999L);
        assertFalse(found.isPresent());
    }

    @Test
    void findByPhoneNumber_existingPhone_returnsEntity() {
        CustomerEntity saved = customerRepositoryImpl.save(testCustomer);

        Optional<CustomerEntity> found = customerRepositoryImpl.findByPhoneNumber("123456789");

        assertTrue(found.isPresent());
        assertEquals("123456789", found.get().getPhoneNumber());
        assertEquals(saved.getId(), found.get().getId());
    }

    @Test
    void findByPhoneNumber_nullPhone_returnsEmpty() {
        Optional<CustomerEntity> found = customerRepositoryImpl.findByPhoneNumber(null);
        assertFalse(found.isPresent());
    }

    @Test
    void findByPhoneNumber_emptyPhone_returnsEmpty() {
        Optional<CustomerEntity> found = customerRepositoryImpl.findByPhoneNumber("");
        assertFalse(found.isPresent());
    }

    @Test
    void findByPhoneNumber_whitespaceOnlyPhone_returnsEmpty() {
        Optional<CustomerEntity> found = customerRepositoryImpl.findByPhoneNumber("   ");
        assertFalse(found.isPresent());
    }

    @Test
    void findByPhoneNumber_nonExistingPhone_returnsEmpty() {
        Optional<CustomerEntity> found = customerRepositoryImpl.findByPhoneNumber("999999999");
        assertFalse(found.isPresent());
    }

    @Test
    void findAll_withMultipleEntities_returnsAllActiveEntities() {
        CustomerEntity customer1 = new CustomerEntity();
        customer1.setFirstName("Test1");
        customer1.setLastName("Customer1");
        customer1.setPhoneNumber("111111111");
        customer1.setActive(true);

        CustomerEntity customer2 = new CustomerEntity();
        customer2.setFirstName("Test2");
        customer2.setLastName("Customer2");
        customer2.setPhoneNumber("222222222");
        customer2.setActive(true);

        customerRepositoryImpl.save(customer1);
        customerRepositoryImpl.save(customer2);

        List<CustomerEntity> all = customerRepositoryImpl.findAll();

        assertNotNull(all);

        boolean hasTestCustomer1FirstName = all.stream().anyMatch(c -> "Test1".equals(c.getFirstName()));
        boolean hasTestCustomer1LastName = all.stream().anyMatch(c -> "Customer1".equals(c.getLastName()));
        boolean hasTestCustomer2FirstName = all.stream().anyMatch(c -> "Test2".equals(c.getFirstName()));
        boolean hasTestCustomer2LastName = all.stream().anyMatch(c -> "Customer2".equals(c.getLastName()));

        assertTrue(hasTestCustomer1FirstName, "Should contain first name 'Test1'");
        assertTrue(hasTestCustomer1LastName, "Should contain last name 'Customer1'");
        assertTrue(hasTestCustomer2FirstName, "Should contain first name 'Test2'");
        assertTrue(hasTestCustomer2LastName, "Should contain last name 'Customer2'");

        assertTrue(all.stream().allMatch(CustomerEntity::isActive), "All returned entities should be active");
    }

    @Test
    void deleteById_existingId_softDeletesEntity() {
        CustomerEntity saved = customerRepositoryImpl.save(testCustomer);
        Long id = saved.getId();

        customerRepositoryImpl.deleteById(id);

        Optional<CustomerEntity> found = customerRepositoryImpl.findById(id);
        assertFalse(found.isPresent(), "Soft deleted entity should not be returned by findById");

        List<CustomerEntity> allActive = customerRepositoryImpl.findAll();
        boolean foundInActiveList = allActive.stream().anyMatch(entity -> id.equals(entity.getId()));
        assertFalse(foundInActiveList, "Soft deleted entity should not appear in findAll results");
    }

    @Test
    void deleteById_nullId_throwsException() {
        assertThrows(InvalidDataAccessApiUsageException.class, () -> {
            customerRepositoryImpl.deleteById(null);
        });
    }

    @Test
    void delete_nullEntity_throwsException() {
        assertThrows(InvalidDataAccessApiUsageException.class, () -> {
            customerRepositoryImpl.delete(null);
        });
    }

    @Test
    void existsById_existingId_returnsTrue() {
        CustomerEntity saved = customerRepositoryImpl.save(testCustomer);

        boolean exists = customerRepositoryImpl.existsById(saved.getId());

        assertTrue(exists);
    }

    @Test
    void existsById_nullId_returnsFalse() {
        boolean exists = customerRepositoryImpl.existsById(null);
        assertFalse(exists);
    }

    @Test
    void existsById_nonExistingId_returnsFalse() {
        boolean exists = customerRepositoryImpl.existsById(999L);
        assertFalse(exists);
    }

    @Test
    void count_afterSavingEntity_increasesCount() {
        long initialCount = customerRepositoryImpl.count();

        customerRepositoryImpl.save(testCustomer);

        long newCount = customerRepositoryImpl.count();
        assertEquals(initialCount + 1, newCount);
    }
}
