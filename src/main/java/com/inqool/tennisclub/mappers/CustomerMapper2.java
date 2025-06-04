package com.inqool.tennisclub.mappers;

import com.inqool.tennisclub.api.CreateCustomerDto;
import com.inqool.tennisclub.data.model.CustomerEntity;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface CustomerMapper2 {

    // CustomerDto toDto(CustomerEntity entity);

    // List<CustomerDto> toDtoList(List<CustomerEntity> entities);

    CustomerEntity toEntity(CreateCustomerDto dto);
}
