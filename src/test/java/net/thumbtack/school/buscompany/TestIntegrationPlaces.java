package net.thumbtack.school.buscompany;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import net.thumbtack.school.buscompany.dto.request.PlaceDtoRequest;
import net.thumbtack.school.buscompany.dto.response.OrderDtoResponse;
import net.thumbtack.school.buscompany.dto.response.PassengerDtoResponse;
import net.thumbtack.school.buscompany.dto.response.PlaceDtoResponse;
import net.thumbtack.school.buscompany.error.Error;
import net.thumbtack.school.buscompany.error.ErrorResponse;
import net.thumbtack.school.buscompany.model.Place;
import net.thumbtack.school.buscompany.repository.PlaceRepository;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class TestIntegrationPlaces extends TestIntegrationBase {

    @Autowired
    private PlaceRepository placeRepository;

    @BeforeAll
    public void beforeAll() throws JsonProcessingException {
        clearAll();
        createUsersInDatabase();
        createTripsInDatabase();
        createOrdersInDatabase();
    }

    @BeforeEach
    public void freePlaces() {
        List<Place> chosenPlaces = placeRepository.findByPassengerIdNotNull();
        chosenPlaces.forEach(place -> place.setPassenger(null));
        placeRepository.saveAll(chosenPlaces);
    }

    @Test
    public void testGetFreePlacesOk() throws JsonProcessingException {
        HttpHeaders headers = loginClient();
        String url = UriComponentsBuilder.fromHttpUrl(fullUrl(ORDERS_URL))
                .queryParam("busName", "Man")
                .encode().toUriString();
        ResponseEntity<String> response = template.exchange(url, HttpMethod.GET, new HttpEntity<>(headers), String.class);
        OrderDtoResponse[] orders = mapper.readValue(response.getBody(), OrderDtoResponse[].class);
        ResponseEntity<String> response2 = template.exchange(fullUrl(PLACES_URL) + "/" + orders[0].getId(),
                HttpMethod.GET, new HttpEntity<>(headers), String.class);
        Integer[] places = new ObjectMapper().readValue(response2.getBody(), Integer[].class);
        assertAll(() -> assertEquals(response2.getStatusCodeValue(), 200),
                () -> assertEquals(places.length, 90));
    }

    @Test
    public void testGetFreePlacesNotExistingOrderId() throws JsonProcessingException {
        HttpHeaders headers = loginClient();
        String url = UriComponentsBuilder.fromHttpUrl(fullUrl(ORDERS_URL)).encode().toUriString();
        ResponseEntity<String> response = template.exchange(url, HttpMethod.GET, new HttpEntity<>(headers), String.class);
        OrderDtoResponse[] orders = mapper.readValue(response.getBody(), OrderDtoResponse[].class);
        int notExistingOrderId = Arrays.stream(orders).mapToInt(OrderDtoResponse::getId).max().orElse(0) + 1;
        HttpClientErrorException exc = assertThrows(HttpClientErrorException.class, ()-> {
            template.exchange(fullUrl(PLACES_URL) + "/" + notExistingOrderId,
                    HttpMethod.GET, new HttpEntity<>(headers), String.class);
        });
        Error error = new ObjectMapper().readValue(exc.getResponseBodyAsString(), ErrorResponse.class).getErrors().get(0);
        assertAll(() -> assertEquals(exc.getRawStatusCode(), 400),
                () -> assertEquals(error.getErrorCode(), "ORDER_NOT_FOUND"),
                () -> assertEquals(error.getField(), "id"));
    }

    @Test
    public void testChoosePlaceOk() throws JsonProcessingException {
        HttpHeaders headers = loginClient();
        ResponseEntity<String> response2 = choosePlaceForFirstOrderFirstPassenger(headers, 1);
        PlaceDtoResponse placeDtoResponse = mapper.readValue(response2.getBody(), PlaceDtoResponse.class);
        assertAll(() -> assertEquals(response2.getStatusCodeValue(), 200),
                () -> assertEquals(placeDtoResponse.getPlace(), 1));
    }

    @Test
    public void testChooseNotExistingPlace() throws JsonProcessingException {
        HttpHeaders headers = loginClient();
        HttpClientErrorException exc = assertThrows(HttpClientErrorException.class, ()-> {
            choosePlaceForFirstOrderFirstPassenger(headers, 100);
        });
        Error error = new ObjectMapper().readValue(exc.getResponseBodyAsString(), ErrorResponse.class).getErrors().get(0);
        assertAll(() -> assertEquals(exc.getRawStatusCode(), 400),
                () -> assertEquals(error.getErrorCode(), "PLACE_NOT_FOUND"),
                () -> assertEquals(error.getField(), "orderId,place"));
    }

    @Test
    public void testChooseAlreadyChosenPlace() throws JsonProcessingException {
        HttpHeaders headers0 = login(loginClient2);
        choosePlaceForFirstOrderFirstPassenger(headers0, 1);
        HttpHeaders headers = loginClient();
        HttpClientErrorException exc = assertThrows(HttpClientErrorException.class, ()-> {
            choosePlaceForFirstOrderFirstPassenger(headers, 1);
        });
        Error error = new ObjectMapper().readValue(exc.getResponseBodyAsString(), ErrorResponse.class).getErrors().get(0);
        assertAll(() -> assertEquals(exc.getRawStatusCode(), 400),
                () -> assertEquals(error.getErrorCode(), "PLACE_NOT_FOUND"),
                () -> assertEquals(error.getField(), "orderId,place"));
    }

    @Test
    public void testChoosePlaceOrderNotFound() throws JsonProcessingException {
        HttpHeaders headers = loginClient();
        String url = UriComponentsBuilder.fromHttpUrl(fullUrl(ORDERS_URL))
                .queryParam("busName", "Man")
                .encode().toUriString();
        ResponseEntity<String> response = template.exchange(url, HttpMethod.GET, new HttpEntity<>(headers), String.class);
        OrderDtoResponse[] orders = mapper.readValue(response.getBody(), OrderDtoResponse[].class);
        int notExistingOrderId = Arrays.stream(orders).mapToInt(OrderDtoResponse::getId).max().orElse(1) + 10;
        PassengerDtoResponse passenger = orders[0].getPassengers().get(0);
        PlaceDtoRequest placeDtoRequest = new PlaceDtoRequest(notExistingOrderId, passenger.getFirstName(),
                passenger.getLastName(), passenger.getPassport(), 1);
        HttpClientErrorException exc = assertThrows(HttpClientErrorException.class, ()-> {
            template.exchange(fullUrl(PLACES_URL), HttpMethod.POST, new HttpEntity<>(placeDtoRequest, headers), String.class);
        });
        Error error = new ObjectMapper().readValue(exc.getResponseBodyAsString(), ErrorResponse.class).getErrors().get(0);
        assertAll(() -> assertEquals(exc.getRawStatusCode(), 400),
                () -> assertEquals(error.getErrorCode(), "ORDER_NOT_FOUND"),
                () -> assertEquals(error.getField(), "id"));
    }

    @Test
    public void testChoosePlaceNoPermission() throws JsonProcessingException {
        PlaceDtoRequest placeDtoRequest = new PlaceDtoRequest(1, "FirstName",
                "LastName", "11111", 1);
        HttpClientErrorException exc = assertThrows(HttpClientErrorException.class, ()-> {
            template.exchange(fullUrl(PLACES_URL), HttpMethod.POST, new HttpEntity<>(placeDtoRequest, loginAdmin()), String.class);
        });
        Error error = new ObjectMapper().readValue(exc.getResponseBodyAsString(), ErrorResponse.class).getErrors().get(0);
        assertAll(() -> assertEquals(exc.getRawStatusCode(), 400),
                () -> assertEquals(error.getErrorCode(), "NO_PERMISSION"),
                () -> assertEquals(error.getField(), "sessionId"));
    }

    @Test
    public void testDeleteOrderFreePlaces() throws JsonProcessingException {
        HttpHeaders headers = login(loginClient2);
        String url = UriComponentsBuilder.fromHttpUrl(fullUrl(ORDERS_URL))
                .queryParam("busName", "Man")
                .encode().toUriString();
        ResponseEntity<String> response = template.exchange(url, HttpMethod.GET, new HttpEntity<>(headers), String.class);
        OrderDtoResponse orderDtoResponse = mapper.readValue(response.getBody(), OrderDtoResponse[].class)[0];
        PassengerDtoResponse passenger = orderDtoResponse.getPassengers().get(0);
        PlaceDtoRequest placeDtoRequest = new PlaceDtoRequest(orderDtoResponse.getId(), passenger.getFirstName(),
                passenger.getLastName(), passenger.getPassport(), 1);
        template.exchange(fullUrl(PLACES_URL), HttpMethod.POST, new HttpEntity<>(placeDtoRequest, headers), String.class);
        template.exchange(fullUrl(ORDERS_URL) + "/" + orderDtoResponse.getId(), HttpMethod.DELETE,
                new HttpEntity<>(headers), String.class);
        HttpHeaders headers1 = loginClient();
        ResponseEntity<String> response1 = template.exchange(url, HttpMethod.GET, new HttpEntity<>(headers1), String.class);
        OrderDtoResponse orderDtoResponse1 = mapper.readValue(response1.getBody(), OrderDtoResponse[].class)[0];
        ResponseEntity<String> response2 = template.exchange(fullUrl(PLACES_URL) + "/" + orderDtoResponse1.getId(),
                HttpMethod.GET, new HttpEntity<>(headers1), String.class);
        Integer[] places = new ObjectMapper().readValue(response2.getBody(), Integer[].class);
        assertAll(() -> assertEquals(response2.getStatusCodeValue(), 200),
                () -> assertEquals(places.length, 90));
    }

    private ResponseEntity<String> choosePlaceForFirstOrderFirstPassenger(HttpHeaders headers, int place) throws JsonProcessingException {
        String url = UriComponentsBuilder.fromHttpUrl(fullUrl(ORDERS_URL))
                .queryParam("busName", "Man")
                .encode().toUriString();
        ResponseEntity<String> response = template.exchange(url, HttpMethod.GET, new HttpEntity<>(headers), String.class);
        OrderDtoResponse[] orders = mapper.readValue(response.getBody(), OrderDtoResponse[].class);
        PassengerDtoResponse passenger = orders[0].getPassengers().get(0);
        PlaceDtoRequest placeDtoRequest = new PlaceDtoRequest(orders[0].getId(), passenger.getFirstName(),
                passenger.getLastName(), passenger.getPassport(), place);
        return template.exchange(fullUrl(PLACES_URL), HttpMethod.POST, new HttpEntity<>(placeDtoRequest, headers), String.class);
    }

}
