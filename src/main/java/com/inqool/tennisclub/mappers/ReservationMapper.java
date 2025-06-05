package com.inqool.tennisclub.mappers;

import com.inqool.tennisclub.api.CreateReservationDto;
import com.inqool.tennisclub.api.ReservationDto;
import com.inqool.tennisclub.data.model.CourtEntity;
import com.inqool.tennisclub.data.model.CustomerEntity;
import com.inqool.tennisclub.data.model.ReservationEntity;
import com.inqool.tennisclub.service.CourtService;
import com.inqool.tennisclub.service.CustomerService;
import com.inqool.tennisclub.service.ReservationService;
import java.math.BigDecimal;
import java.util.List;
import org.mapstruct.Context;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

@Mapper(
        componentModel = "spring",
        uses = {CustomerService.class, CourtService.class})
public interface ReservationMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "customer", source = ".", qualifiedByName = "findOrCreateCustomer")
    @Mapping(target = "court", source = "courtNumber", qualifiedByName = "findCourt")
    ReservationEntity toReservationEntity(
            CreateReservationDto dto, @Context CustomerService customerService, @Context CourtService courtService);

    @Named("findOrCreateCustomer")
    default CustomerEntity findOrCreateCustomer(CreateReservationDto dto, @Context CustomerService customerService) {
        CustomerEntity customer = new CustomerEntity();
        customer.setName(dto.getCustomerName());
        customer.setPhoneNumber(dto.getPhoneNumber());

        return customerService.createIfNotExist(customer);
    }

    @Named("findCourt")
    default CourtEntity findCourt(Integer courtNumber, @Context CourtService courtService) {
        return courtService.findByCourtNumber(courtNumber);
    }

    @Mapping(target = "courtId", source = "court.id")
    @Mapping(target = "courtNumber", source = "court.courtNumber")
    @Mapping(target = "customerName", source = "customer.name")
    @Mapping(target = "phoneNumber", source = "customer.phoneNumber")
    @Mapping(target = "totalPrice", source = ".", qualifiedByName = "calculatePrice")
    ReservationDto toDto(ReservationEntity entity, @Context ReservationService reservationService);

    @Named("calculatePrice")
    default BigDecimal calculatePrice(ReservationEntity entity, @Context ReservationService reservationService) {
        return reservationService.calculateTotalPrice(entity);
    }

    List<ReservationDto> toDtoList(List<ReservationEntity> entities, @Context ReservationService reservationService);
}
