package com.inqool.tennisclub.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

import com.inqool.tennisclub.data.model.CustomerEntity;
import com.inqool.tennisclub.data.repository.CustomerRepository;
import com.inqool.tennisclub.exceptions.EntityNotFoundException;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class CustomerServiceTest {

    @Mock
    private CustomerRepository customerRepository;

    @InjectMocks
    private CustomerService customerService;

    private CustomerEntity testEntity;
    private CustomerEntity secondEntity;

    @BeforeEach
    void setUp() {
        testEntity = new CustomerEntity();
        testEntity.setId(1L);
        testEntity.setPhoneNumber("+420777123456");
        testEntity.setFirstName("Jan");
        testEntity.setLastName("Novák");
        testEntity.setActive(true);

        secondEntity = new CustomerEntity();
        secondEntity.setId(2L);
        secondEntity.setPhoneNumber("+420608987654");
        secondEntity.setFirstName("Marie");
        secondEntity.setLastName("Svobodová");
        secondEntity.setActive(true);
    }

    @Test
    void create_validEntity_returnsCreatedEntity() {
        CustomerEntity newEntity = new CustomerEntity();
        newEntity.setPhoneNumber("+420775111222");
        newEntity.setFirstName("Petr");
        newEntity.setLastName("Dvořák");

        CustomerEntity savedEntity = new CustomerEntity();
        savedEntity.setId(3L);
        savedEntity.setPhoneNumber("+420775111222");
        savedEntity.setFirstName("Petr");
        savedEntity.setLastName("Dvořák");
        savedEntity.setActive(true);

        when(customerRepository.save(newEntity)).thenReturn(savedEntity);

        CustomerEntity result = customerService.create(newEntity);

        assertThat(result).isEqualTo(savedEntity);
        assertThat(result.getId()).isEqualTo(3L);
        verify(customerRepository, times(1)).save(newEntity);
    }

    @Test
    void createIfNotExist_existingCustomer_returnsExistingEntity() {
        CustomerEntity entityToCreate = new CustomerEntity();
        entityToCreate.setPhoneNumber("+420777123456");
        entityToCreate.setFirstName("Different");
        entityToCreate.setLastName("Name");

        when(customerRepository.findByPhoneNumber("+420777123456")).thenReturn(Optional.of(testEntity));

        CustomerEntity result = customerService.createIfNotExist(entityToCreate);

        assertThat(result).isEqualTo(testEntity);
        assertThat(result.getFirstName()).isEqualTo("Jan");
        verify(customerRepository, times(1)).findByPhoneNumber("+420777123456");
        verify(customerRepository, never()).save(any());
    }

    @Test
    void createIfNotExist_nonExistingCustomer_createsNewEntity() {
        CustomerEntity entityToCreate = new CustomerEntity();
        entityToCreate.setPhoneNumber("+420775333444");
        entityToCreate.setFirstName("New");
        entityToCreate.setLastName("Customer");

        CustomerEntity savedEntity = new CustomerEntity();
        savedEntity.setId(4L);
        savedEntity.setPhoneNumber("+420775333444");
        savedEntity.setFirstName("New");
        savedEntity.setLastName("Customer");
        savedEntity.setActive(true);

        when(customerRepository.findByPhoneNumber("+420775333444")).thenReturn(Optional.empty());
        when(customerRepository.save(entityToCreate)).thenReturn(savedEntity);

        CustomerEntity result = customerService.createIfNotExist(entityToCreate);

        assertThat(result).isEqualTo(savedEntity);
        assertThat(result.getId()).isEqualTo(4L);
        verify(customerRepository, times(1)).findByPhoneNumber("+420775333444");
        verify(customerRepository, times(1)).save(entityToCreate);
    }

    @Test
    void findById_existingId_returnsEntity() {
        Long id = 1L;

        when(customerRepository.findById(id)).thenReturn(Optional.of(testEntity));

        Optional<CustomerEntity> result = customerService.findById(id);

        assertThat(result).isPresent();
        assertThat(result.get()).isEqualTo(testEntity);
        verify(customerRepository, times(1)).findById(id);
    }

    @Test
    void findById_nonExistingId_throwsEntityNotFoundException() {
        Long id = 99L;

        when(customerRepository.findById(id)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> customerService.findById(id))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessage("Customer with id " + id + " not found");

        verify(customerRepository, times(1)).findById(id);
    }

    @Test
    void findByPhoneNumber_existingPhoneNumber_returnsEntity() {
        String phoneNumber = "+420777123456";

        when(customerRepository.findByPhoneNumber(phoneNumber)).thenReturn(Optional.of(testEntity));

        Optional<CustomerEntity> result = customerService.findByPhoneNumber(phoneNumber);

        assertThat(result).isPresent();
        assertThat(result.get()).isEqualTo(testEntity);
        verify(customerRepository, times(1)).findByPhoneNumber(phoneNumber);
    }

    @Test
    void findByPhoneNumber_nonExistingPhoneNumber_throwsEntityNotFoundException() {
        String phoneNumber = "+420999888777";

        when(customerRepository.findByPhoneNumber(phoneNumber)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> customerService.findByPhoneNumber(phoneNumber))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessage("Customer with phoneNumber " + phoneNumber + " not found");

        verify(customerRepository, times(1)).findByPhoneNumber(phoneNumber);
    }

    @Test
    void findAll_returnsListOfEntities() {
        List<CustomerEntity> entities = List.of(testEntity, secondEntity);

        when(customerRepository.findAll()).thenReturn(entities);

        List<CustomerEntity> result = customerService.findAll();

        assertThat(result).hasSize(2);
        assertThat(result).containsExactly(testEntity, secondEntity);
        verify(customerRepository, times(1)).findAll();
    }

    @Test
    void findAll_emptyRepository_returnsEmptyList() {
        List<CustomerEntity> entities = List.of();

        when(customerRepository.findAll()).thenReturn(entities);

        List<CustomerEntity> result = customerService.findAll();

        assertThat(result).isEmpty();
        verify(customerRepository, times(1)).findAll();
    }

    @Test
    void update_existingEntity_returnsUpdatedEntity() {
        CustomerEntity entityToUpdate = new CustomerEntity();
        entityToUpdate.setId(1L);
        entityToUpdate.setPhoneNumber("+420777123456");
        entityToUpdate.setFirstName("Updated Jan");
        entityToUpdate.setLastName("Updated Novák");
        entityToUpdate.setActive(true);

        when(customerRepository.findById(1L)).thenReturn(Optional.of(testEntity));
        when(customerRepository.save(entityToUpdate)).thenReturn(entityToUpdate);

        CustomerEntity result = customerService.update(entityToUpdate);

        assertThat(result).isEqualTo(entityToUpdate);
        assertThat(result.getFirstName()).isEqualTo("Updated Jan");
        assertThat(result.getLastName()).isEqualTo("Updated Novák");
        verify(customerRepository, times(1)).findById(1L);
        verify(customerRepository, times(1)).save(entityToUpdate);
    }

    @Test
    void update_nonExistingEntity_throwsEntityNotFoundException() {
        CustomerEntity entityToUpdate = new CustomerEntity();
        entityToUpdate.setId(99L);
        entityToUpdate.setPhoneNumber("+420999888777");

        when(customerRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> customerService.update(entityToUpdate))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessage("Customer with id " + 99L + " not found");

        verify(customerRepository, times(1)).findById(99L);
        verify(customerRepository, never()).save(any());
    }

    @Test
    void deleteById_existingId_deletesEntity() {
        Long id = 1L;

        when(customerRepository.findById(id)).thenReturn(Optional.of(testEntity));
        doNothing().when(customerRepository).deleteById(id);

        customerService.deleteById(id);

        verify(customerRepository, times(1)).findById(id);
        verify(customerRepository, times(1)).deleteById(id);
    }

    @Test
    void deleteById_nonExistingId_throwsEntityNotFoundException() {
        Long id = 99L;

        when(customerRepository.findById(id)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> customerService.deleteById(id))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessage("Customer with id " + id + " not found");

        verify(customerRepository, times(1)).findById(id);
        verify(customerRepository, never()).deleteById(any());
    }

    @Test
    void create_entityWithNullValues_handlesGracefully() {
        CustomerEntity entityWithNulls = new CustomerEntity();
        entityWithNulls.setPhoneNumber("+420724555666");
        entityWithNulls.setFirstName("Test");
        entityWithNulls.setLastName(null);

        CustomerEntity savedEntity = new CustomerEntity();
        savedEntity.setId(5L);
        savedEntity.setPhoneNumber("+420724555666");
        savedEntity.setFirstName("Test");
        savedEntity.setLastName(null);
        savedEntity.setActive(true);

        when(customerRepository.save(entityWithNulls)).thenReturn(savedEntity);

        CustomerEntity result = customerService.create(entityWithNulls);

        assertThat(result).isEqualTo(savedEntity);
        assertThat(result.getLastName()).isNull();
        verify(customerRepository, times(1)).save(entityWithNulls);
    }
}
