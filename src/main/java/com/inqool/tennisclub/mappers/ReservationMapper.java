package com.inqool.tennisclub.mappers;

import com.inqool.tennisclub.api.CreateReservationDto;
import com.inqool.tennisclub.api.ReservationDto;
import com.inqool.tennisclub.data.model.ReservationEntity;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ReservationMapper {

    private final CustomerMapper customerMapper;

    @Autowired
    public ReservationMapper(CustomerMapper customerMapper) {
        this.customerMapper = customerMapper;
    }

    public ReservationDto toDto(ReservationEntity entity) {
        ReservationDto dto = new ReservationDto();
        dto.setId(entity.getId());
        dto.setCourtId(entity.getCourt().getId());
        dto.setPhoneNumber(entity.getCustomer().getPhoneNumber());
        dto.setCustomerName(entity.getCustomer().getFirstName());
        dto.setCourtNumber(entity.getCourt().getCourtNumber());
        dto.setGameType(entity.getGameType());
        dto.setStartTime(entity.getStartTime());
        dto.setEndTime(entity.getEndTime());
        // dto.setTotalPrice(entity.getTotalPrice()); // This will be set in facade
        dto.setCreatedAt(entity.getCreatedAt());

        return dto;
    }

    public ReservationEntity toEntity(CreateReservationDto dto) {
        ReservationEntity entity = new ReservationEntity();

        entity.setCustomer(customerMapper.toCustomerEntity(dto));
        entity.setGameType(dto.getGameType());
        // entity.setCourt(dto.getCourtNumber()); TODO + Valid?
        entity.setStartTime(dto.getStartTime());
        entity.setEndTime(dto.getEndTime());

        return entity;
    }

    public List<ReservationDto> toDtoList(List<ReservationEntity> all) {
        return all.stream().map(this::toDto).collect(Collectors.toList());
    }
}
