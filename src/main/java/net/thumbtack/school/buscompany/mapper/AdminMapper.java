package net.thumbtack.school.buscompany.mapper;

import net.thumbtack.school.buscompany.dto.request.AdminDtoRequest;
import net.thumbtack.school.buscompany.dto.response.AdminDtoResponse;
import net.thumbtack.school.buscompany.model.Admin;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface AdminMapper {

    AdminMapper INSTANCE = Mappers.getMapper(AdminMapper.class);

    Admin dtoToAdmin(AdminDtoRequest request);

    AdminDtoResponse adminToDto(Admin admin);

}
