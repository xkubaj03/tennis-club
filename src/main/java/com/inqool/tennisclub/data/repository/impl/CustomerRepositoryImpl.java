package com.inqool.tennisclub.data.repository.impl;

import com.inqool.tennisclub.data.model.CustomerEntity;
import com.inqool.tennisclub.data.repository.CustomerRepository;
import jakarta.persistence.TypedQuery;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Repository;

@Repository
public class CustomerRepositoryImpl extends BaseRepositoryImpl<CustomerEntity, Long> implements CustomerRepository {

    public CustomerRepositoryImpl() {
        super(CustomerEntity.class);
    }

    @Override
    public Optional<CustomerEntity> findByPhoneNumber(String phoneNumber) {
        if (phoneNumber == null || phoneNumber.trim().isEmpty()) {
            return Optional.empty();
        }
        String queryStr = "SELECT c FROM CustomerEntity c WHERE c.phoneNumber = :phoneNumber AND c.active = true";
        TypedQuery<CustomerEntity> query = entityManager.createQuery(queryStr, CustomerEntity.class);
        query.setParameter("phoneNumber", phoneNumber);

        List<CustomerEntity> results = query.getResultList();
        return results.isEmpty() ? Optional.empty() : Optional.of(results.get(0));
    }
}
