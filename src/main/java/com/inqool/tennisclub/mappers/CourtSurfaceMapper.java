package com.inqool.tennisclub.mappers;

import com.inqool.tennisclub.api.CourtSurfaceDto;
import com.inqool.tennisclub.api.CreateCourtSurfaceDto;
import com.inqool.tennisclub.data.model.CourtSurfaceEntity;
import java.util.List;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface CourtSurfaceMapper {

    CourtSurfaceDto toDto(CourtSurfaceEntity entity);

    List<CourtSurfaceDto> toDtoList(List<CourtSurfaceEntity> entities);

    @Mapping(target = "id", ignore = true)
    CourtSurfaceEntity toEntity(CreateCourtSurfaceDto dto);
}
