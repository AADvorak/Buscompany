package net.thumbtack.school.buscompany;

import com.fasterxml.jackson.core.JsonProcessingException;
import net.thumbtack.school.buscompany.dto.response.TripDtoResponse;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.web.util.UriComponentsBuilder;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class TestIntegrationGetTrips extends TestIntegrationBase {

    @BeforeAll
    public void beforeAll() throws JsonProcessingException {
        clearAll();
        createUsersInDatabase();
        createTripsInDatabase();
    }

    @Test
    public void testGetTripsNoParams() throws JsonProcessingException {
        String url = UriComponentsBuilder.fromHttpUrl(fullUrl(TRIPS_URL))
                .encode().toUriString();
        ResponseEntity<String> response = template.exchange(url, HttpMethod.GET, new HttpEntity<>(loginAdmin()), String.class);
        TripDtoResponse[] trips = mapper.readValue(response.getBody(), TripDtoResponse[].class);
        assertAll(() -> assertEquals(response.getStatusCodeValue(), 200),
                () -> assertEquals(trips.length, 5));
    }

    @Test
    public void testGetTripsDates4Match() throws JsonProcessingException {
        String url = UriComponentsBuilder.fromHttpUrl(fullUrl(TRIPS_URL))
                .queryParam("fromDate", createDate(4, 1))
                .queryParam("toDate", createDate(5, 1))
                .encode().toUriString();
        ResponseEntity<String> response = template.exchange(url, HttpMethod.GET, new HttpEntity<>(loginAdmin()), String.class);
        TripDtoResponse[] trips = mapper.readValue(response.getBody(), TripDtoResponse[].class);
        assertAll(() -> assertEquals(response.getStatusCodeValue(), 200),
                () -> assertEquals(trips.length, 4));
    }

    @Test
    public void testGetTripsDatesNoMatchFromDate() throws JsonProcessingException {
        String url = UriComponentsBuilder.fromHttpUrl(fullUrl(TRIPS_URL))
                .queryParam("fromDate", createDate(8, 1))
                .queryParam("toDate", createDate(9, 1))
                .encode().toUriString();
        ResponseEntity<String> response = template.exchange(url, HttpMethod.GET, new HttpEntity<>(loginAdmin()), String.class);
        TripDtoResponse[] trips = mapper.readValue(response.getBody(), TripDtoResponse[].class);
        assertAll(() -> assertEquals(response.getStatusCodeValue(), 200),
                () -> assertEquals(trips.length, 0));
    }

    @Test
    public void testGetTripsDatesNoMatchToDate() throws JsonProcessingException {
        String url = UriComponentsBuilder.fromHttpUrl(fullUrl(TRIPS_URL))
                .queryParam("fromDate", createDate(2, 1))
                .queryParam("toDate", createDate(3, 1))
                .encode().toUriString();
        ResponseEntity<String> response = template.exchange(url, HttpMethod.GET, new HttpEntity<>(loginAdmin()), String.class);
        TripDtoResponse[] trips = mapper.readValue(response.getBody(), TripDtoResponse[].class);
        assertAll(() -> assertEquals(response.getStatusCodeValue(), 200),
                () -> assertEquals(trips.length, 0));
    }

    @Test
    public void testGetTripsBusNameNoMatch() throws JsonProcessingException {
        String url = UriComponentsBuilder.fromHttpUrl(fullUrl(TRIPS_URL))
                .queryParam("busName", "Scania")
                .encode().toUriString();
        ResponseEntity<String> response = template.exchange(url, HttpMethod.GET, new HttpEntity<>(loginAdmin()), String.class);
        TripDtoResponse[] trips = mapper.readValue(response.getBody(), TripDtoResponse[].class);
        assertAll(() -> assertEquals(response.getStatusCodeValue(), 200),
                () -> assertEquals(trips.length, 0));
    }

    @Test
    public void testGetTripsBusName3Match() throws JsonProcessingException {
        String url = UriComponentsBuilder.fromHttpUrl(fullUrl(TRIPS_URL))
                .queryParam("busName", "Mercedes")
                .encode().toUriString();
        ResponseEntity<String> response = template.exchange(url, HttpMethod.GET, new HttpEntity<>(loginAdmin()), String.class);
        TripDtoResponse[] trips = mapper.readValue(response.getBody(), TripDtoResponse[].class);
        assertAll(() -> assertEquals(response.getStatusCodeValue(), 200),
                () -> assertEquals(trips.length, 3));
    }

    @Test
    public void testGetTripsFromStation4Match() throws JsonProcessingException {
        String url = UriComponentsBuilder.fromHttpUrl(fullUrl(TRIPS_URL))
                .queryParam("fromStation", "Саратов")
                .encode().toUriString();
        ResponseEntity<String> response = template.exchange(url, HttpMethod.GET, new HttpEntity<>(loginAdmin()), String.class);
        TripDtoResponse[] trips = mapper.readValue(response.getBody(), TripDtoResponse[].class);
        assertAll(() -> assertEquals(response.getStatusCodeValue(), 200),
                () -> assertEquals(trips.length, 4));
    }

    @Test
    public void testGetTripsFromStationNoMatch() throws JsonProcessingException {
        String url = UriComponentsBuilder.fromHttpUrl(fullUrl(TRIPS_URL))
                .queryParam("fromStation", "Саратов-1")
                .encode().toUriString();
        ResponseEntity<String> response = template.exchange(url, HttpMethod.GET, new HttpEntity<>(loginAdmin()), String.class);
        TripDtoResponse[] trips = mapper.readValue(response.getBody(), TripDtoResponse[].class);
        assertAll(() -> assertEquals(response.getStatusCodeValue(), 200),
                () -> assertEquals(trips.length, 0));
    }

    @Test
    public void testGetTripsToStation1Match() throws JsonProcessingException {
        String url = UriComponentsBuilder.fromHttpUrl(fullUrl(TRIPS_URL))
                .queryParam("toStation", "Саратов")
                .encode().toUriString();
        ResponseEntity<String> response = template.exchange(url, HttpMethod.GET, new HttpEntity<>(loginAdmin()), String.class);
        TripDtoResponse[] trips = mapper.readValue(response.getBody(), TripDtoResponse[].class);
        assertAll(() -> assertEquals(response.getStatusCodeValue(), 200),
                () -> assertEquals(trips.length, 1));
    }

    @Test
    public void testGetTripsToStationNoMatch() throws JsonProcessingException {
        String url = UriComponentsBuilder.fromHttpUrl(fullUrl(TRIPS_URL))
                .queryParam("toStation", "Саратов-1")
                .encode().toUriString();
        ResponseEntity<String> response = template.exchange(url, HttpMethod.GET, new HttpEntity<>(loginAdmin()), String.class);
        TripDtoResponse[] trips = mapper.readValue(response.getBody(), TripDtoResponse[].class);
        assertAll(() -> assertEquals(response.getStatusCodeValue(), 200),
                () -> assertEquals(trips.length, 0));
    }

    @Test
    public void testGetTripsFromStationToStation2Match() throws JsonProcessingException {
        String url = UriComponentsBuilder.fromHttpUrl(fullUrl(TRIPS_URL))
                .queryParam("fromStation", "Саратов")
                .queryParam("toStation", "Вольск")
                .encode().toUriString();
        ResponseEntity<String> response = template.exchange(url, HttpMethod.GET, new HttpEntity<>(loginAdmin()), String.class);
        TripDtoResponse[] trips = mapper.readValue(response.getBody(), TripDtoResponse[].class);
        assertAll(() -> assertEquals(response.getStatusCodeValue(), 200),
                () -> assertEquals(trips.length, 2));
    }

    @Test
    public void testGetTripsAllParams1Match() throws JsonProcessingException {
        String url = UriComponentsBuilder.fromHttpUrl(fullUrl(TRIPS_URL))
                .queryParam("fromDate", createDate(4, 1))
                .queryParam("toDate", createDate(5, 1))
                .queryParam("fromStation", "Саратов")
                .queryParam("toStation", "Пенза")
                .queryParam("busName", "Mercedes")
                .encode().toUriString();
        ResponseEntity<String> response = template.exchange(url, HttpMethod.GET, new HttpEntity<>(loginAdmin()), String.class);
        TripDtoResponse[] trips = mapper.readValue(response.getBody(), TripDtoResponse[].class);
        assertAll(() -> assertEquals(response.getStatusCodeValue(), 200),
                () -> assertEquals(trips.length, 1));
    }

    @Test
    public void testGetTripsAllParamsNoMatchBusName() throws JsonProcessingException {
        String url = UriComponentsBuilder.fromHttpUrl(fullUrl(TRIPS_URL))
                .queryParam("fromDate", createDate(4, 1))
                .queryParam("toDate",createDate(5, 1))
                .queryParam("fromStation", "Саратов")
                .queryParam("toStation", "Пенза")
                .queryParam("busName", "Man")
                .encode().toUriString();
        ResponseEntity<String> response = template.exchange(url, HttpMethod.GET, new HttpEntity<>(loginAdmin()), String.class);
        TripDtoResponse[] trips = mapper.readValue(response.getBody(), TripDtoResponse[].class);
        assertAll(() -> assertEquals(response.getStatusCodeValue(), 200),
                () -> assertEquals(trips.length, 0));
    }

}
