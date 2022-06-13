package net.thumbtack.school.buscompany.error;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum BusAppErrorCode {

    WRONG_LOGIN_PASSWORD(new ErrorDescription("login", "Wrong login and password pair")),
    WRONG_OLD_PASSWORD(new ErrorDescription("oldPassword", "Wrong old password")),
    SESSION_NOT_FOUND(new ErrorDescription("sessionId", "User session not found")),
    TRIP_NOT_FOUND(new ErrorDescription("id", "Trip not found")),
    BUS_NOT_FOUND(new ErrorDescription("busId", "Bus not found")),
    ORDER_NOT_FOUND(new ErrorDescription("id", "Order not found")),
    PASSENGER_NOT_FOUND(new ErrorDescription("firstName,lastName,passport", "Passenger not found")),
    PLACE_NOT_FOUND(new ErrorDescription("orderId,place", "Place not found")),
    TRIP_DATE_NOT_FOUND(new ErrorDescription("tripId,date", "Trip date not found")),
    TRIP_IS_NOT_EDITABLE(new ErrorDescription("id", "Trip is not editable")),
    TRIP_IS_NOT_APPROVED(new ErrorDescription("id", "Trip is not approved")),
    NO_PERMISSION(new ErrorDescription("sessionId", "User has no permission")),
    LOGIN_ALREADY_EXIST(new ErrorDescription("login", "Login already exist")),
    EMPTY_TRIP_DATES(new ErrorDescription("schedule,dates", "Empty trip dates"));

    private final ErrorDescription errorDescription;

}
