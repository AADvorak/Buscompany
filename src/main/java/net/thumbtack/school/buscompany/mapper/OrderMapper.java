package net.thumbtack.school.buscompany.mapper;

import net.thumbtack.school.buscompany.dto.request.OrderDtoRequest;
import net.thumbtack.school.buscompany.dto.response.OrderDtoResponse;
import net.thumbtack.school.buscompany.model.Order;
import org.mapstruct.*;
import org.mapstruct.factory.Mappers;

import java.math.BigDecimal;

@Mapper
public abstract class OrderMapper {

    public static OrderMapper INSTANCE = Mappers.getMapper(OrderMapper.class);

    @AfterMapping
    protected void convertNameToUpperCase(@MappingTarget OrderDtoResponse orderDtoResponse) {
        orderDtoResponse.setTotalPrice(orderDtoResponse.getPrice().multiply(BigDecimal.valueOf(orderDtoResponse.getPassengers().size())));
    }

    @Mappings({
            @Mapping(target = "tripId", source = "order.tripDate.trip.id"),
            @Mapping(target = "fromStation", source = "order.tripDate.trip.fromStation"),
            @Mapping(target = "toStation", source = "order.tripDate.trip.toStation"),
            @Mapping(target = "busName", source = "order.tripDate.trip.bus.busName"),
            @Mapping(target = "date", source = "order.tripDate.date", dateFormat = "yyyy-MM-dd"),
            @Mapping(target = "start", source = "order.tripDate.trip.start", dateFormat = "hh:mm"),
            @Mapping(target = "duration", source = "order.tripDate.trip.duration", qualifiedByName = "timeStrFromMinutes"),
            @Mapping(target = "price", source = "order.tripDate.trip.price"),
    })
    public abstract OrderDtoResponse orderToDto(Order order);

    public abstract Order dtoToOrder(OrderDtoRequest dto);

    @Named("timeStrFromMinutes")
    public static String timeStrFromMinutes(int value) {
        return MapperUtils.timeStrFromMinutes(value);
    }

}
