package com.squad20.sistema_climbe.mapper;

import com.squad20.sistema_climbe.dto.UserDTO;
import com.squad20.sistema_climbe.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface UserMapper {

    UserDTO toDTO(User user);


    @Mapping(target = "passwordHash", ignore = true)
    @Mapping(target = "permissions", ignore = true)
    User toEntity(UserDTO dto);
}
