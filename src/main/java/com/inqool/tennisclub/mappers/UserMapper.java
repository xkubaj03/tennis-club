package com.inqool.tennisclub.mappers;

import com.inqool.tennisclub.api.auth.LoginDto;
import com.inqool.tennisclub.api.auth.RegisterDto;
import com.inqool.tennisclub.data.model.UserEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface UserMapper {
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "active", ignore = true)
    UserEntity toEntity(LoginDto dto);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "active", ignore = true)
    UserEntity toEntity(RegisterDto dto);
}
