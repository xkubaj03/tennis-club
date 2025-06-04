package com.inqool.tennisclub.mappers;

import com.inqool.tennisclub.api.CourtDto;
import com.inqool.tennisclub.api.CreateCourtDto;
import com.inqool.tennisclub.data.model.CourtEntity;
import java.util.List;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface CourtMapper {

    @Mapping(target = "surfaceId", source = "courtSurface.id")
    CourtDto toDto(CourtEntity entity);

    List<CourtDto> toDtoList(List<CourtEntity> entities);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "reservations", ignore = true)
    @Mapping(target = "courtSurface", ignore = true)
    @Mapping(target = "active", ignore = true)
    @Mapping(target = "courtSurface.id", source = "surfaceId")
    CourtEntity toEntity(CreateCourtDto dto);
}
