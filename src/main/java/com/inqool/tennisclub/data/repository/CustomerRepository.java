package com.inqool.tennisclub.data.repository;

import com.inqool.tennisclub.data.model.CustomerEntity;
import java.util.Optional;

/**
 * Repository interface for Customer entities
 */
public interface CustomerRepository extends BaseRepository<CustomerEntity, Long> {

    /**
     * Find customer by phone number
     */
    Optional<CustomerEntity> findByPhoneNumber(String phoneNumber);
}
