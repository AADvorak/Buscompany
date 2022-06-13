package net.thumbtack.school.buscompany.mapper;

import net.thumbtack.school.buscompany.dto.response.BusDtoResponse;
import net.thumbtack.school.buscompany.model.Bus;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface BusMapper {

    BusMapper INSTANCE = Mappers.getMapper(BusMapper.class);

    BusDtoResponse busToDto(Bus bus);

}
