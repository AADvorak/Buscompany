package net.thumbtack.school.buscompany.mapper;

import net.thumbtack.school.buscompany.dto.response.UserDtoResponse;
import net.thumbtack.school.buscompany.model.User;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface UserMapper {

    UserMapper INSTANCE = Mappers.getMapper(UserMapper.class);

    UserDtoResponse userToDto(User user);

}
