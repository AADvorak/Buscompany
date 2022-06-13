package net.thumbtack.school.buscompany.service;

import net.thumbtack.school.buscompany.ApplicationProperties;
import net.thumbtack.school.buscompany.dto.request.OrderDtoRequest;
import net.thumbtack.school.buscompany.dto.response.OrderDtoResponse;
import net.thumbtack.school.buscompany.error.BusAppErrorCode;
import net.thumbtack.school.buscompany.error.BusAppException;
import net.thumbtack.school.buscompany.mapper.OrderMapper;
import net.thumbtack.school.buscompany.model.*;
import net.thumbtack.school.buscompany.repository.*;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class OrderService extends ServiceBase {

    private final OrderRepository orderRepository;

    private final ClientRepository clientRepository;

    private final TripDateRepository tripDateRepository;

    public OrderService(UserSessionRepository userSessionRepository, ApplicationProperties applicationProperties,
                        OrderRepository orderRepository, ClientRepository clientRepository,
                        TripDateRepository tripDateRepository) {
        super(userSessionRepository, applicationProperties);
        this.orderRepository = orderRepository;
        this.clientRepository = clientRepository;
        this.tripDateRepository = tripDateRepository;
    }

    @Transactional
    public OrderDtoResponse insertOrder(String sessionId, OrderDtoRequest request) throws BusAppException {
        User user = getUserBySessionId(sessionId);
        if (!(user instanceof Client)) {
            throw new BusAppException(BusAppErrorCode.NO_PERMISSION);
        }
        TripDate tripDate = tripDateRepository.findByTripIdAndDate(request.getTripId(), LocalDate.parse(request.getDate()));
        if (tripDate == null || !tripDate.getTrip().isApproved()) {
            throw new BusAppException(BusAppErrorCode.TRIP_DATE_NOT_FOUND);
        }
        int updated = tripDateRepository.checkAndDecreaseFreePlaceCount(tripDate.getId(), request.getPassengers().size());
        if (updated == 0) {
            throw new BusAppException(BusAppErrorCode.TRIP_DATE_NOT_FOUND);
        }
        Order order = OrderMapper.INSTANCE.dtoToOrder(request);
        order.setTripDate(tripDate);
        order.setClient((Client) user);
        return OrderMapper.INSTANCE.orderToDto(orderRepository.save(order));
    }

    public List<OrderDtoResponse> findOrders(String sessionId, String fromStation, String toStation, String busName,
                                             LocalDate fromDate, LocalDate toDate, Integer clientId) throws BusAppException {
        User user = getUserBySessionId(sessionId);
        Client client = null;
        if (user instanceof Client) {
            client = (Client) user;
        } else if (clientId != null) {
            client = clientRepository.getById(clientId);
        }
        Specification<Order> specification = Specification
                .where(client == null ? null : OrderSpecifications.clientIs(client))
                .and(fromDate == null ? null : OrderSpecifications.dateIsAfter(fromDate))
                .and(toDate == null ? null : OrderSpecifications.dateIsBefore(toDate))
                .and(fromStation == null ? null : OrderSpecifications.fromStationLike(fromStation))
                .and(toStation == null ? null : OrderSpecifications.toStationLike(toStation))
                .and(busName == null ? null : OrderSpecifications.busNameLike(busName));
        return orderRepository.findAll(specification).stream().map(OrderMapper.INSTANCE::orderToDto).collect(Collectors.toList());
    }

    @Transactional
    public void deleteOrder(String sessionId, int orderId) throws BusAppException {
        User user = getUserBySessionId(sessionId);
        Order order = orderRepository.findByIdAndClientId(orderId, user.getId());
        if (order == null) {
            throw new BusAppException(BusAppErrorCode.ORDER_NOT_FOUND);
        }
        tripDateRepository.increaseFreePlaceCount(order.getTripDate().getId(), order.getPassengers().size());
        orderRepository.delete(order);
    }
}
