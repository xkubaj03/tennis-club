package com.inqool.tennisclub.mappers;

import com.inqool.tennisclub.api.CreateReservationDto;
import com.inqool.tennisclub.data.model.CustomerEntity;
import org.springframework.stereotype.Component;

@Component
public class CustomerMapper {

    public CustomerEntity toCustomerEntity(CreateReservationDto dto) {
        CustomerEntity customerEntity = new CustomerEntity();
        customerEntity.setPhoneNumber(dto.getPhoneNumber());
        customerEntity.setFirstName(dto.getCustomerName());

        return customerEntity;
    }
}
