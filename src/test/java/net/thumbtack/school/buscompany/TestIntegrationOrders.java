package net.thumbtack.school.buscompany;

import com.fasterxml.jackson.core.JsonProcessingException;
import net.thumbtack.school.buscompany.dto.request.OrderDtoRequest;
import net.thumbtack.school.buscompany.dto.response.OrderDtoResponse;
import net.thumbtack.school.buscompany.dto.response.TripDtoResponse;
import net.thumbtack.school.buscompany.error.Error;
import net.thumbtack.school.buscompany.error.ErrorResponse;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.web.client.HttpClientErrorException;

import java.math.BigDecimal;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class TestIntegrationOrders extends TestIntegrationBase {

    @BeforeAll
    public void beforeAll() throws JsonProcessingException {
        clearAll();
        createUsersInDatabase();
        createTripsInDatabase();
    }

    @BeforeEach
    public void beforeEach() {
        clearOrders();
    }

    @Test
    public void testPostOrderOk() throws JsonProcessingException {
        HttpHeaders headers = loginClient();
        ResponseEntity<String> response = template.exchange(fullUrl(TRIPS_URL), HttpMethod.GET, new HttpEntity<>(headers), String.class);
        TripDtoResponse tripDtoResponse = (mapper.readValue(response.getBody(), TripDtoResponse[].class))[0];
        OrderDtoRequest orderDtoRequest = createOrderForFirstDateOfTrip(tripDtoResponse, 2);
        ResponseEntity<String> response2 = template.exchange(fullUrl(ORDERS_URL), HttpMethod.POST,
                new HttpEntity<>(orderDtoRequest, headers), String.class);
        OrderDtoResponse orderDtoResponse = mapper.readValue(response2.getBody(), OrderDtoResponse.class);
        assertAll(() -> assertEquals(response2.getStatusCodeValue(), 200),
                () -> assertEquals(orderDtoResponse.getTripId(), tripDtoResponse.getId()),
                () -> assertEquals(orderDtoResponse.getTotalPrice(),
                        tripDtoResponse.getPrice().multiply(BigDecimal.valueOf(orderDtoRequest.getPassengers().size()))));
    }

    @Test
    public void testPostOrderInvalidTripId() throws JsonProcessingException {
        OrderDtoRequest orderDtoRequest = createOrder(0, createDate(4, 1), 2);
        HttpClientErrorException exc = assertThrows(HttpClientErrorException.class, ()-> {
            template.exchange(fullUrl(ORDERS_URL), HttpMethod.POST, new HttpEntity<>(orderDtoRequest, loginClient()), String.class);
        });
        Error error = mapper.readValue(exc.getResponseBodyAsString(), ErrorResponse.class).getErrors().get(0);
        assertAll(() -> assertEquals(exc.getRawStatusCode(), 400),
                () -> assertEquals(error.getErrorCode(), "Positive"),
                () -> assertEquals(error.getField(), "tripId"));
    }

    @Test
    public void testPostOrderInvalidDate() throws JsonProcessingException {
        OrderDtoRequest orderDtoRequest = createOrder(1, null, 2);
        HttpClientErrorException exc = assertThrows(HttpClientErrorException.class, ()-> {
            template.exchange(fullUrl(ORDERS_URL), HttpMethod.POST, new HttpEntity<>(orderDtoRequest, loginClient()), String.class);
        });
        Error error = mapper.readValue(exc.getResponseBodyAsString(), ErrorResponse.class).getErrors().get(0);
        assertAll(() -> assertEquals(exc.getRawStatusCode(), 400),
                () -> assertEquals(error.getErrorCode(), "NotNull"),
                () -> assertEquals(error.getField(), "date"));
    }

    @Test
    public void testPostOrderEmptyPassengers() throws JsonProcessingException {
        OrderDtoRequest orderDtoRequest = createOrder(1, createDate(4, 1), 0);
        HttpClientErrorException exc = assertThrows(HttpClientErrorException.class, ()-> {
            template.exchange(fullUrl(ORDERS_URL), HttpMethod.POST, new HttpEntity<>(orderDtoRequest, loginClient()), String.class);
        });
        Error error = mapper.readValue(exc.getResponseBodyAsString(), ErrorResponse.class).getErrors().get(0);
        assertAll(() -> assertEquals(exc.getRawStatusCode(), 400),
                () -> assertEquals(error.getErrorCode(), "NotEmpty"),
                () -> assertEquals(error.getField(), "passengers"));
    }

    @Test
    public void testPostOrderNoPermission() throws JsonProcessingException {
        OrderDtoRequest orderDtoRequest = createOrder(1, createDate(4, 1), 2);
        HttpClientErrorException exc = assertThrows(HttpClientErrorException.class, ()-> {
            template.exchange(fullUrl(ORDERS_URL), HttpMethod.POST, new HttpEntity<>(orderDtoRequest, loginAdmin()), String.class);
        });
        Error error = mapper.readValue(exc.getResponseBodyAsString(), ErrorResponse.class).getErrors().get(0);
        assertAll(() -> assertEquals(exc.getRawStatusCode(), 400),
                () -> assertEquals(error.getField(), "sessionId"),
                () -> assertEquals(error.getErrorCode(), "NO_PERMISSION"));
    }

    @Test
    public void testPostOrderNotApprovedTrip() throws JsonProcessingException {
        HttpHeaders headers = loginClient();
        ResponseEntity<String> response = template.exchange(fullUrl(TRIPS_URL), HttpMethod.GET, new HttpEntity<>(headers), String.class);
        TripDtoResponse[] tripDtoResponses = mapper.readValue(response.getBody(), TripDtoResponse[].class);
        int notApprovedTripId = Arrays.stream(tripDtoResponses).mapToInt(TripDtoResponse::getId).max().orElse(1) + 1;
        OrderDtoRequest orderDtoRequest = createOrder(notApprovedTripId, createDate(4, 1), 2);
        HttpClientErrorException exc = assertThrows(HttpClientErrorException.class, ()-> {
            template.exchange(fullUrl(ORDERS_URL), HttpMethod.POST, new HttpEntity<>(orderDtoRequest, headers), String.class);
        });
        Error error = mapper.readValue(exc.getResponseBodyAsString(), ErrorResponse.class).getErrors().get(0);
        assertAll(() -> assertEquals(exc.getRawStatusCode(), 400),
                () -> assertEquals(error.getField(), "tripId,date"),
                () -> assertEquals(error.getErrorCode(), "TRIP_DATE_NOT_FOUND"));
    }

    @Test
    public void testPostOrderNotExistingTripId() throws JsonProcessingException {
        HttpHeaders headers = loginClient();
        ResponseEntity<String> response = template.exchange(fullUrl(TRIPS_URL), HttpMethod.GET, new HttpEntity<>(headers), String.class);
        TripDtoResponse[] tripDtoResponses = mapper.readValue(response.getBody(), TripDtoResponse[].class);
        int notExistingTripId = Arrays.stream(tripDtoResponses).mapToInt(TripDtoResponse::getId).max().orElse(1) + 10;
        OrderDtoRequest orderDtoRequest = createOrder(notExistingTripId, createDate(4, 1), 2);
        HttpClientErrorException exc = assertThrows(HttpClientErrorException.class, ()-> {
            template.exchange(fullUrl(ORDERS_URL), HttpMethod.POST, new HttpEntity<>(orderDtoRequest, headers), String.class);
        });
        Error error = mapper.readValue(exc.getResponseBodyAsString(), ErrorResponse.class).getErrors().get(0);
        assertAll(() -> assertEquals(exc.getRawStatusCode(), 400),
                () -> assertEquals(error.getField(), "tripId,date"),
                () -> assertEquals(error.getErrorCode(), "TRIP_DATE_NOT_FOUND"));
    }

    @Test
    public void testPostOrderNotExistingDate() throws JsonProcessingException {
        HttpHeaders headers = loginClient();
        ResponseEntity<String> response = template.exchange(fullUrl(TRIPS_URL), HttpMethod.GET, new HttpEntity<>(headers), String.class);
        TripDtoResponse tripDtoResponse = (mapper.readValue(response.getBody(), TripDtoResponse[].class))[0];
        OrderDtoRequest orderDtoRequest = createOrder(tripDtoResponse.getId(), createDate(1, 1), 2);
        HttpClientErrorException exc = assertThrows(HttpClientErrorException.class, ()-> {
            template.exchange(fullUrl(ORDERS_URL), HttpMethod.POST, new HttpEntity<>(orderDtoRequest, headers), String.class);
        });
        Error error = mapper.readValue(exc.getResponseBodyAsString(), ErrorResponse.class).getErrors().get(0);
        assertAll(() -> assertEquals(exc.getRawStatusCode(), 400),
                () -> assertEquals(error.getField(), "tripId,date"),
                () -> assertEquals(error.getErrorCode(), "TRIP_DATE_NOT_FOUND"));
    }

    @Test
    public void testPostOrderNotEnoughPlaces() throws JsonProcessingException {
        HttpHeaders headers = loginClient();
        ResponseEntity<String> response = template.exchange(fullUrl(TRIPS_URL), HttpMethod.GET, new HttpEntity<>(headers), String.class);
        TripDtoResponse tripDtoResponse = (mapper.readValue(response.getBody(), TripDtoResponse[].class))[0];
        int placeCount = tripDtoResponse.getBus().getPlaceCount();
        OrderDtoRequest orderDtoRequest = createOrderForFirstDateOfTrip(tripDtoResponse, placeCount - 1);
        template.exchange(fullUrl(ORDERS_URL), HttpMethod.POST,
                new HttpEntity<>(orderDtoRequest, headers), String.class);
        OrderDtoRequest orderDtoRequest2 = createOrderForFirstDateOfTrip(tripDtoResponse, 2);
        HttpClientErrorException exc = assertThrows(HttpClientErrorException.class, ()-> {
            template.exchange(fullUrl(ORDERS_URL), HttpMethod.POST, new HttpEntity<>(orderDtoRequest2, headers), String.class);
        });
        Error error = mapper.readValue(exc.getResponseBodyAsString(), ErrorResponse.class).getErrors().get(0);
        assertAll(() -> assertEquals(exc.getRawStatusCode(), 400),
                () -> assertEquals(error.getField(), "tripId,date"),
                () -> assertEquals(error.getErrorCode(), "TRIP_DATE_NOT_FOUND"));
    }

    @Test
    public void testDeleteOrderNotFoundNotExisting() throws JsonProcessingException {
        HttpClientErrorException exc = assertThrows(HttpClientErrorException.class, ()-> {
            template.exchange(fullUrl(ORDERS_URL) + "/" + 1, HttpMethod.DELETE, new HttpEntity<>(loginClient()), String.class);
        });
        Error error = mapper.readValue(exc.getResponseBodyAsString(), ErrorResponse.class).getErrors().get(0);
        assertAll(() -> assertEquals(exc.getRawStatusCode(), 400),
                () -> assertEquals(error.getField(), "id"),
                () -> assertEquals(error.getErrorCode(), "ORDER_NOT_FOUND"));
    }

    @Test
    public void testDeleteOrderNotFoundByClientCheck() throws JsonProcessingException {
        HttpHeaders headers = login(loginClient2);
        ResponseEntity<String> response = template.exchange(fullUrl(TRIPS_URL), HttpMethod.GET, new HttpEntity<>(headers), String.class);
        TripDtoResponse tripDtoResponse = mapper.readValue(response.getBody(), TripDtoResponse[].class)[0];
        ResponseEntity<String> response1 = template.exchange(fullUrl(ORDERS_URL), HttpMethod.POST,
                new HttpEntity<>(createOrderForFirstDateOfTrip(tripDtoResponse, 2), headers), String.class);
        int orderId = mapper.readValue(response1.getBody(), OrderDtoResponse.class).getId();
        HttpClientErrorException exc = assertThrows(HttpClientErrorException.class, ()-> {
            template.exchange(fullUrl(ORDERS_URL) + "/" + orderId, HttpMethod.DELETE, new HttpEntity<>(loginClient()), String.class);
        });
        Error error = mapper.readValue(exc.getResponseBodyAsString(), ErrorResponse.class).getErrors().get(0);
        assertAll(() -> assertEquals(exc.getRawStatusCode(), 400),
                () -> assertEquals(error.getField(), "id"),
                () -> assertEquals(error.getErrorCode(), "ORDER_NOT_FOUND"));
    }

    @Test
    public void testDeleteOrderOk() throws JsonProcessingException {
        HttpHeaders headers = loginClient();
        ResponseEntity<String> response = template.exchange(fullUrl(TRIPS_URL), HttpMethod.GET, new HttpEntity<>(headers), String.class);
        TripDtoResponse tripDtoResponse = mapper.readValue(response.getBody(), TripDtoResponse[].class)[0];
        ResponseEntity<String> response1 = template.exchange(fullUrl(ORDERS_URL), HttpMethod.POST,
                new HttpEntity<>(createOrderForFirstDateOfTrip(tripDtoResponse, 2), headers), String.class);
        int orderId = mapper.readValue(response1.getBody(), OrderDtoResponse.class).getId();
        ResponseEntity<String> response2 = template.exchange(fullUrl(ORDERS_URL) + "/" + orderId, HttpMethod.DELETE,
                new HttpEntity<>(headers), String.class);
        assertEquals(response2.getStatusCodeValue(), 200);
    }

    @Test
    public void testDeleteOrderFreePlaces() throws JsonProcessingException {
        HttpHeaders headers = loginClient();
        ResponseEntity<String> response = template.exchange(fullUrl(TRIPS_URL), HttpMethod.GET, new HttpEntity<>(headers), String.class);
        TripDtoResponse tripDtoResponse = mapper.readValue(response.getBody(), TripDtoResponse[].class)[0];
        OrderDtoRequest orderDtoRequest = createOrderForFirstDateOfTrip(tripDtoResponse, tripDtoResponse.getBus().getPlaceCount());
        ResponseEntity<String> response1 = template.exchange(fullUrl(ORDERS_URL), HttpMethod.POST,
                new HttpEntity<>(orderDtoRequest, headers), String.class);
        int orderId = mapper.readValue(response1.getBody(), OrderDtoResponse.class).getId();
        template.exchange(fullUrl(ORDERS_URL) + "/" + orderId, HttpMethod.DELETE,
                new HttpEntity<>(headers), String.class);
        ResponseEntity<String> response2 = template.exchange(fullUrl(ORDERS_URL), HttpMethod.POST,
                new HttpEntity<>(orderDtoRequest, headers), String.class);
        OrderDtoResponse orderDtoResponse = mapper.readValue(response2.getBody(), OrderDtoResponse.class);
        assertAll(() -> assertEquals(response2.getStatusCodeValue(), 200),
                () -> assertEquals(orderDtoResponse.getTripId(), tripDtoResponse.getId()),
                () -> assertEquals(orderDtoResponse.getTotalPrice(),
                        tripDtoResponse.getPrice().multiply(BigDecimal.valueOf(orderDtoRequest.getPassengers().size()))));
    }

}
