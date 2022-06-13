package net.thumbtack.school.buscompany.service;

import net.thumbtack.school.buscompany.ApplicationProperties;
import net.thumbtack.school.buscompany.dto.request.PlaceDtoRequest;
import net.thumbtack.school.buscompany.dto.response.PlaceDtoResponse;
import net.thumbtack.school.buscompany.error.BusAppErrorCode;
import net.thumbtack.school.buscompany.error.BusAppException;
import net.thumbtack.school.buscompany.model.*;
import net.thumbtack.school.buscompany.repository.OrderRepository;
import net.thumbtack.school.buscompany.repository.PlaceRepository;
import net.thumbtack.school.buscompany.repository.UserSessionRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class PlaceService extends ServiceBase {

    private final PlaceRepository placeRepository;

    private final OrderRepository orderRepository;

    public PlaceService(UserSessionRepository userSessionRepository, ApplicationProperties applicationProperties,
                        PlaceRepository placeRepository, OrderRepository orderRepository) {
        super(userSessionRepository, applicationProperties);
        this.placeRepository = placeRepository;
        this.orderRepository = orderRepository;
    }

    public List<Integer> getFreePlaces(String sessionId, int orderId) throws BusAppException {
        User user = getUserBySessionId(sessionId);
        if (!(user instanceof Client)) {
            throw new BusAppException(BusAppErrorCode.NO_PERMISSION);
        }
        Order order = orderRepository.findByIdAndClientId(orderId, user.getId());
        if (order == null) {
            throw new BusAppException(BusAppErrorCode.ORDER_NOT_FOUND);
        }
        return placeRepository.findByTripDateIdAndPassengerIdNull(order.getTripDate().getId()).stream()
                .map(Place::getNumber).collect(Collectors.toList());
    }

    @Transactional
    public PlaceDtoResponse choosePlace(String sessionId, PlaceDtoRequest request) throws BusAppException {
        User user = getUserBySessionId(sessionId);
        if (!(user instanceof Client)) {
            throw new BusAppException(BusAppErrorCode.NO_PERMISSION);
        }
        Order order = orderRepository.findByIdAndClientId(request.getOrderId(), user.getId());
        if (order == null) {
            throw new BusAppException(BusAppErrorCode.ORDER_NOT_FOUND);
        }
        Passenger passenger = order.getPassengers().stream().filter(item ->
                        Objects.equals(item.getFirstName(), request.getFirstName()) &&
                                Objects.equals(item.getLastName(), request.getLastName()) &&
                                Objects.equals(item.getPassport(), request.getPassport())).findAny()
                .orElseThrow(() -> new BusAppException(BusAppErrorCode.PASSENGER_NOT_FOUND));
        Place passengersOldPlace = placeRepository.findByTripDateIdAndPassengerId(order.getTripDate().getId(),
                passenger.getId());
        int updated = placeRepository.setPassengerToFreePlace(passenger.getId(), order.getTripDate().getId(), request.getPlace());
        if (updated == 0) {
            throw new BusAppException(BusAppErrorCode.PLACE_NOT_FOUND);
        }
        if (passengersOldPlace != null) {
            passengersOldPlace.setPassenger(null);
            placeRepository.save(passengersOldPlace);
        }
        return new PlaceDtoResponse(order.getId(), passenger.getFirstName(), passenger.getLastName(),
                passenger.getPassport(), request.getPlace(),
                "Билет_" + order.getTripDate().getId() + "_" + request.getPlace());
    }

}
