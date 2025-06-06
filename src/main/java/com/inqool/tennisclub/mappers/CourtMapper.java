package com.inqool.tennisclub.mappers;

import com.inqool.tennisclub.api.CourtDto;
import com.inqool.tennisclub.api.CreateCourtDto;
import com.inqool.tennisclub.data.model.CourtEntity;
import com.inqool.tennisclub.data.model.CourtSurfaceEntity;
import com.inqool.tennisclub.service.CourtSurfaceService;
import java.util.List;
import org.mapstruct.Context;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

@Mapper(
        componentModel = "spring",
        uses = {CourtSurfaceService.class})
public interface CourtMapper {

    @Mapping(target = "surfaceId", source = "courtSurface.id")
    CourtDto toDto(CourtEntity entity);

    List<CourtDto> toDtoList(List<CourtEntity> entities);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "reservations", ignore = true)
    @Mapping(target = "active", ignore = true)
    @Mapping(target = "courtSurface", source = "surfaceId", qualifiedByName = "findCourtSurface")
    CourtEntity toEntity(CreateCourtDto dto, @Context CourtSurfaceService courtSurface);

    @Named("findCourtSurface")
    default CourtSurfaceEntity findCourtSurface(Long id, @Context CourtSurfaceService courtSurfaceService) {
        return courtSurfaceService.findById(id);
    }
}
