package net.thumbtack.school.buscompany.mapper;

import net.thumbtack.school.buscompany.dto.request.ScheduleDtoRequest;
import net.thumbtack.school.buscompany.dto.response.ScheduleDtoResponse;
import net.thumbtack.school.buscompany.model.TripSchedule;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface TripScheduleMapper {

    TripScheduleMapper INSTANCE = Mappers.getMapper(TripScheduleMapper.class);

    TripSchedule dtoToTripSchedule(ScheduleDtoRequest dto);

    ScheduleDtoResponse tripScheduleToDto(TripSchedule tripSchedule);

}
