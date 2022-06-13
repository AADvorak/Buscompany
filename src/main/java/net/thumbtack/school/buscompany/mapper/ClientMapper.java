package net.thumbtack.school.buscompany.mapper;

import net.thumbtack.school.buscompany.dto.request.ClientDtoRequest;
import net.thumbtack.school.buscompany.dto.response.ClientDtoResponse;
import net.thumbtack.school.buscompany.model.Client;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface ClientMapper {

    ClientMapper INSTANCE = Mappers.getMapper(ClientMapper.class);

    Client dtoToClient(ClientDtoRequest request);

    ClientDtoResponse clientToDto(Client client);

}
