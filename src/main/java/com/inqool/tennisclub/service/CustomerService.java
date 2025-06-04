package com.inqool.tennisclub.service;

import com.inqool.tennisclub.data.model.CustomerEntity;
import com.inqool.tennisclub.data.repository.CustomerRepository;
import com.inqool.tennisclub.exceptions.EntityNotFoundException;
import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CustomerService {

    private final CustomerRepository customerRepository;

    @Autowired
    public CustomerService(CustomerRepository customerRepository) {
        this.customerRepository = customerRepository;
    }

    public CustomerEntity create(CustomerEntity entity) {
        return customerRepository.save(entity);
    }

    public CustomerEntity createIfNotExist(CustomerEntity entity) {
        return customerRepository
                .findByPhoneNumber(entity.getPhoneNumber())
                .orElseGet(() -> customerRepository.save(entity));
    }

    public Optional<CustomerEntity> findById(Long id) {
        return Optional.ofNullable(customerRepository
                .findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Customer with id " + id + " not found")));
    }

    public Optional<CustomerEntity> findByPhoneNumber(String phoneNumber) {
        return Optional.ofNullable(customerRepository
                .findByPhoneNumber(phoneNumber)
                .orElseThrow(
                        () -> new EntityNotFoundException("Customer with phoneNumber " + phoneNumber + " not found")));
    }

    public List<CustomerEntity> findAll() {
        return customerRepository.findAll();
    }

    public CustomerEntity update(CustomerEntity entity) {
        customerRepository
                .findById(entity.getId())
                .orElseThrow(() -> new EntityNotFoundException("Customer with id " + entity.getId() + " not found"));

        return customerRepository.save(entity);
    }

    public void deleteById(Long id) {
        customerRepository
                .findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Customer with id " + id + " not found"));

        customerRepository.deleteById(id);
    }
}
