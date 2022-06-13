package net.thumbtack.school.buscompany.mapper;

import net.thumbtack.school.buscompany.dto.request.PassengerDtoRequest;
import net.thumbtack.school.buscompany.dto.response.PassengerDtoResponse;
import net.thumbtack.school.buscompany.model.Passenger;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface PassengerMapper {

    PassengerMapper INSTANCE = Mappers.getMapper(PassengerMapper.class);

    Passenger dtoToPassenger(PassengerDtoRequest dto);

    PassengerDtoResponse passengerToDto(Passenger passenger);

}
