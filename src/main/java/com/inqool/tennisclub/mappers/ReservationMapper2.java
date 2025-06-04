package com.inqool.tennisclub.mappers;

import com.inqool.tennisclub.api.CreateReservationDto;
import com.inqool.tennisclub.data.model.CourtEntity;
import com.inqool.tennisclub.data.model.CustomerEntity;
import com.inqool.tennisclub.data.model.ReservationEntity;
import com.inqool.tennisclub.service.CourtService;
import com.inqool.tennisclub.service.CustomerService;
import org.mapstruct.Context;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

@Mapper(
        componentModel = "spring",
        uses = {CustomerService.class, CourtService.class})
public interface ReservationMapper2 {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "customer", source = ".", qualifiedByName = "findOrCreateCustomer")
    @Mapping(target = "court", source = "courtNumber", qualifiedByName = "findCourt")
    ReservationEntity toReservationEntity(
            CreateReservationDto dto, @Context CustomerService customerService, @Context CourtService courtService);

    @Named("findOrCreateCustomer")
    default CustomerEntity findOrCreateCustomer(CreateReservationDto dto, @Context CustomerService customerService) {
        CustomerEntity customer = new CustomerEntity();
        customer.setFirstName(dto.getCustomerName());
        customer.setPhoneNumber(dto.getPhoneNumber());

        return customerService.createIfNotExist(customer);
    }

    @Named("findCourt")
    default CourtEntity findCourt(Integer courtNumber, @Context CourtService courtService) {
        return courtService.findByCourtNumber(courtNumber).orElseThrow();
    }
}
