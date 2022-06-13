package net.thumbtack.school.buscompany;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import net.thumbtack.school.buscompany.dto.request.*;
import net.thumbtack.school.buscompany.dto.response.BusDtoResponse;
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
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class TestIntegrationTrips extends TestIntegrationBase {

    @BeforeAll
    public void beforeAll() {
        clearAll();
        createUsersInDatabase();
    }

    @BeforeEach
    public void deleteTrips() {
        tripRepository.deleteAll();
    }

    @Test
    public void testGetAllBusesOk() throws JsonProcessingException {
        ResponseEntity<String> response1 = template.exchange("http://localhost:8080/api/buses", HttpMethod.GET,
                new HttpEntity<>(loginAdmin()), String.class);
        BusDtoResponse[] busDtoResponse = new ObjectMapper().readValue(response1.getBody(), BusDtoResponse[].class);
        assertAll(() -> assertEquals(response1.getStatusCodeValue(), 200),
                () -> assertTrue(busDtoResponse.length > 0));
    }

    @Test
    public void testPostTripInvalidStart1() throws JsonProcessingException {
        TripDtoRequest tripDtoRequest = createTripWithSchedule("daily");
        tripDtoRequest.setStart("10");
        HttpClientErrorException exc = assertThrows(HttpClientErrorException.class, ()-> {
            template.exchange(fullUrl(TRIPS_URL), HttpMethod.POST,
                    new HttpEntity<>(tripDtoRequest, loginAdmin()), String.class);
        });
        Error error = new ObjectMapper().readValue(exc.getResponseBodyAsString(), ErrorResponse.class).getErrors().get(0);
        assertAll(() -> assertEquals(exc.getRawStatusCode(), 400),
                () -> assertEquals(error.getErrorCode(), "Pattern"),
                () -> assertEquals(error.getField(), "start"));
    }

    @Test
    public void testPostTripInvalidStart2() throws JsonProcessingException {
        TripDtoRequest tripDtoRequest = createTripWithSchedule("daily");
        tripDtoRequest.setStart("24:00");
        HttpClientErrorException exc = assertThrows(HttpClientErrorException.class, ()-> {
            template.exchange(fullUrl(TRIPS_URL), HttpMethod.POST,
                    new HttpEntity<>(tripDtoRequest, loginAdmin()), String.class);
        });
        Error error = new ObjectMapper().readValue(exc.getResponseBodyAsString(), ErrorResponse.class).getErrors().get(0);
        assertAll(() -> assertEquals(exc.getRawStatusCode(), 400),
                () -> assertEquals(error.getErrorCode(), "Pattern"),
                () -> assertEquals(error.getField(), "start"));
    }

    @Test
    public void testPostTripInvalidPrice1() throws JsonProcessingException {
        TripDtoRequest tripDtoRequest = createTripWithSchedule("daily");
        tripDtoRequest.setPrice(BigDecimal.valueOf(0.0));
        HttpClientErrorException exc = assertThrows(HttpClientErrorException.class, ()-> {
            template.exchange(fullUrl(TRIPS_URL), HttpMethod.POST,
                    new HttpEntity<>(tripDtoRequest, loginAdmin()), String.class);
        });
        Error error = new ObjectMapper().readValue(exc.getResponseBodyAsString(), ErrorResponse.class).getErrors().get(0);
        assertAll(() -> assertEquals(exc.getRawStatusCode(), 400),
                () -> assertEquals(error.getErrorCode(), "DecimalMin"),
                () -> assertEquals(error.getField(), "price"));
    }

    @Test
    public void testPostTripInvalidPrice2() throws JsonProcessingException {
        TripDtoRequest tripDtoRequest = createTripWithSchedule("daily");
        tripDtoRequest.setPrice(BigDecimal.valueOf(120.003));
        HttpClientErrorException exc = assertThrows(HttpClientErrorException.class, ()-> {
            template.exchange(fullUrl(TRIPS_URL), HttpMethod.POST,
                    new HttpEntity<>(tripDtoRequest, loginAdmin()), String.class);
        });
        Error error = new ObjectMapper().readValue(exc.getResponseBodyAsString(), ErrorResponse.class).getErrors().get(0);
        assertAll(() -> assertEquals(exc.getRawStatusCode(), 400),
                () -> assertEquals(error.getErrorCode(), "Digits"),
                () -> assertEquals(error.getField(), "price"));
    }

    @Test
    public void testPostTripInvalidDate() throws JsonProcessingException {
        TripDtoRequest tripDtoRequest = createTripWithDates();
        tripDtoRequest.setDates(List.of("20222-01-01"));
        HttpClientErrorException exc = assertThrows(HttpClientErrorException.class, ()-> {
            template.exchange(fullUrl(TRIPS_URL), HttpMethod.POST,
                    new HttpEntity<>(tripDtoRequest, loginAdmin()), String.class);
        });
        Error error = new ObjectMapper().readValue(exc.getResponseBodyAsString(), ErrorResponse.class).getErrors().get(0);
        assertAll(() -> assertEquals(exc.getRawStatusCode(), 400),
                () -> assertEquals(error.getErrorCode(), "Pattern"),
                () -> assertEquals(error.getField(), "dates[0]"));
    }

    @Test
    public void testPostTripNotUniqueDates() throws JsonProcessingException {
        TripDtoRequest tripDtoRequest = createTripWithDates();
        tripDtoRequest.setDates(List.of("2022-01-01", "2022-01-01"));
        HttpClientErrorException exc = assertThrows(HttpClientErrorException.class, ()-> {
            template.exchange(fullUrl(TRIPS_URL), HttpMethod.POST,
                    new HttpEntity<>(tripDtoRequest, loginAdmin()), String.class);
        });
        Error error = new ObjectMapper().readValue(exc.getResponseBodyAsString(), ErrorResponse.class).getErrors().get(0);
        assertAll(() -> assertEquals(exc.getRawStatusCode(), 400),
                () -> assertEquals(error.getErrorCode(), "UniqueElements"),
                () -> assertEquals(error.getField(), "dates"));
    }

    @Test
    public void testPostTripWithDatesAndSchedule() throws JsonProcessingException {
        TripDtoRequest tripDtoRequest = createTripWithDates();
        tripDtoRequest.setSchedule(createSchedule("daily"));
        HttpClientErrorException exc = assertThrows(HttpClientErrorException.class, ()-> {
            template.exchange(fullUrl(TRIPS_URL), HttpMethod.POST,
                    new HttpEntity<>(tripDtoRequest, loginAdmin()), String.class);
        });
        Error error = new ObjectMapper().readValue(exc.getResponseBodyAsString(), ErrorResponse.class).getErrors().get(0);
        assertAll(() -> assertEquals(exc.getRawStatusCode(), 400),
                () -> assertEquals(error.getErrorCode(), "OneFieldNotNull"),
                () -> assertEquals(error.getField(), "tripDtoRequest"));
    }

    @Test
    public void testPostTripNoDatesNoSchedule() throws JsonProcessingException {
        TripDtoRequest tripDtoRequest = createTripWithDates();
        tripDtoRequest.setDates(null);
        HttpClientErrorException exc = assertThrows(HttpClientErrorException.class, ()-> {
            template.exchange(fullUrl(TRIPS_URL), HttpMethod.POST,
                    new HttpEntity<>(tripDtoRequest, loginAdmin()), String.class);
        });
        Error error = new ObjectMapper().readValue(exc.getResponseBodyAsString(), ErrorResponse.class).getErrors().get(0);
        assertAll(() -> assertEquals(exc.getRawStatusCode(), 400),
                () -> assertEquals(error.getErrorCode(), "OneFieldNotNull"),
                () -> assertEquals(error.getField(), "tripDtoRequest"));
    }

    @Test
    public void testPostTripWrongSchedulePeriod1() throws JsonProcessingException {
        TripDtoRequest tripDtoRequest = createTripWithSchedule("wrong");
        HttpClientErrorException exc = assertThrows(HttpClientErrorException.class, ()-> {
            template.exchange(fullUrl(TRIPS_URL), HttpMethod.POST,
                    new HttpEntity<>(tripDtoRequest, loginAdmin()), String.class);
        });
        Error error = new ObjectMapper().readValue(exc.getResponseBodyAsString(), ErrorResponse.class).getErrors().get(0);
        assertAll(() -> assertEquals(exc.getRawStatusCode(), 400),
                () -> assertEquals(error.getErrorCode(), "Period"),
                () -> assertEquals(error.getField(), "schedule.period"));
    }

    @Test
    public void testPostTripWrongSchedulePeriod2() throws JsonProcessingException {
        TripDtoRequest tripDtoRequest = createTripWithSchedule("");
        HttpClientErrorException exc = assertThrows(HttpClientErrorException.class, ()-> {
            template.exchange(fullUrl(TRIPS_URL), HttpMethod.POST,
                    new HttpEntity<>(tripDtoRequest, loginAdmin()), String.class);
        });
        Error error = new ObjectMapper().readValue(exc.getResponseBodyAsString(), ErrorResponse.class).getErrors().get(0);
        assertAll(() -> assertEquals(exc.getRawStatusCode(), 400),
                () -> assertEquals(error.getErrorCode(), "Period"),
                () -> assertEquals(error.getField(), "schedule.period"));
    }

    @Test
    public void testPostTripWrongSchedulePeriod3() throws JsonProcessingException {
        TripDtoRequest tripDtoRequest = createTripWithSchedule(null);
        HttpClientErrorException exc = assertThrows(HttpClientErrorException.class, ()-> {
            template.exchange(fullUrl(TRIPS_URL), HttpMethod.POST,
                    new HttpEntity<>(tripDtoRequest, loginAdmin()), String.class);
        });
        Error error = new ObjectMapper().readValue(exc.getResponseBodyAsString(), ErrorResponse.class).getErrors().get(0);
        assertAll(() -> assertEquals(exc.getRawStatusCode(), 400),
                () -> assertEquals(error.getErrorCode(), "Period"),
                () -> assertEquals(error.getField(), "schedule.period"));
    }

    @Test
    public void testPostTripWrongScheduleDates() throws JsonProcessingException {
        TripDtoRequest tripDtoRequest = createTripWithSchedule("daily");
        tripDtoRequest.getSchedule().setFromDate(createDate(4, 10));
        tripDtoRequest.getSchedule().setToDate(createDate(4, 9));
        HttpClientErrorException exc = assertThrows(HttpClientErrorException.class, ()-> {
            template.exchange(fullUrl(TRIPS_URL), HttpMethod.POST,
                    new HttpEntity<>(tripDtoRequest, loginAdmin()), String.class);
        });
        Error error = new ObjectMapper().readValue(exc.getResponseBodyAsString(), ErrorResponse.class).getErrors().get(0);
        assertAll(() -> assertEquals(exc.getRawStatusCode(), 400),
                () -> assertEquals(error.getErrorCode(), "Date2AfterDate1"),
                () -> assertEquals(error.getField(), "schedule"));
    }

    @Test
    public void testPostTripWrongSchedulePeriodDaysOfMonthAndWeek() throws JsonProcessingException {
        TripDtoRequest tripDtoRequest = createTripWithSchedule("1,2,3,Sat");
        HttpClientErrorException exc = assertThrows(HttpClientErrorException.class, ()-> {
            template.exchange(fullUrl(TRIPS_URL), HttpMethod.POST,
                    new HttpEntity<>(tripDtoRequest, loginAdmin()), String.class);
        });
        Error error = new ObjectMapper().readValue(exc.getResponseBodyAsString(), ErrorResponse.class).getErrors().get(0);
        assertAll(() -> assertEquals(exc.getRawStatusCode(), 400),
                () -> assertEquals(error.getErrorCode(), "Period"),
                () -> assertEquals(error.getField(), "schedule.period"));
    }

    @Test
    public void testPostTripWrongSchedulePeriodDaysOfMonth() throws JsonProcessingException {
        TripDtoRequest tripDtoRequest = createTripWithSchedule("1,2,3,32");
        HttpClientErrorException exc = assertThrows(HttpClientErrorException.class, ()-> {
            template.exchange(fullUrl(TRIPS_URL), HttpMethod.POST,
                    new HttpEntity<>(tripDtoRequest, loginAdmin()), String.class);
        });
        Error error = new ObjectMapper().readValue(exc.getResponseBodyAsString(), ErrorResponse.class).getErrors().get(0);
        assertAll(() -> assertEquals(exc.getRawStatusCode(), 400),
                () -> assertEquals(error.getErrorCode(), "Period"),
                () -> assertEquals(error.getField(), "schedule.period"));
    }

    @Test
    public void testPostTripWrongScheduleNotUniqueDaysOfMonth() throws JsonProcessingException {
        TripDtoRequest tripDtoRequest = createTripWithSchedule("1,2,3,3");
        HttpClientErrorException exc = assertThrows(HttpClientErrorException.class, ()-> {
            template.exchange(fullUrl(TRIPS_URL), HttpMethod.POST,
                    new HttpEntity<>(tripDtoRequest, loginAdmin()), String.class);
        });
        Error error = new ObjectMapper().readValue(exc.getResponseBodyAsString(), ErrorResponse.class).getErrors().get(0);
        assertAll(() -> assertEquals(exc.getRawStatusCode(), 400),
                () -> assertEquals(error.getErrorCode(), "Period"),
                () -> assertEquals(error.getField(), "schedule.period"));
    }

    @Test
    public void testPostTripWrongScheduleNotUniqueDaysOfWeek() throws JsonProcessingException {
        TripDtoRequest tripDtoRequest = createTripWithSchedule("Mon,Wed,Wed");
        HttpClientErrorException exc = assertThrows(HttpClientErrorException.class, ()-> {
            template.exchange(fullUrl(TRIPS_URL), HttpMethod.POST,
                    new HttpEntity<>(tripDtoRequest, loginAdmin()), String.class);
        });
        Error error = new ObjectMapper().readValue(exc.getResponseBodyAsString(), ErrorResponse.class).getErrors().get(0);
        assertAll(() -> assertEquals(exc.getRawStatusCode(), 400),
                () -> assertEquals(error.getErrorCode(), "Period"),
                () -> assertEquals(error.getField(), "schedule.period"));
    }

    @Test
    public void testPostTripWrongScheduleEmptyDates() throws JsonProcessingException {
        TripDtoRequest tripDtoRequest = createTripWithSchedule(createDate(4, 1), createDate(4, 6),"Thu");
        HttpClientErrorException exc = assertThrows(HttpClientErrorException.class, ()-> {
            template.exchange(fullUrl(TRIPS_URL), HttpMethod.POST,
                    new HttpEntity<>(tripDtoRequest, loginAdmin()), String.class);
        });
        Error error = new ObjectMapper().readValue(exc.getResponseBodyAsString(), ErrorResponse.class).getErrors().get(0);
        assertAll(() -> assertEquals(exc.getRawStatusCode(), 400),
                () -> assertEquals(error.getErrorCode(), "EMPTY_TRIP_DATES"),
                () -> assertEquals(error.getField(), "schedule,dates"));
    }

    @Test
    public void testPostTripNoPermission() throws JsonProcessingException {
        TripDtoRequest tripDtoRequest = createTripWithDates();
        HttpClientErrorException exc = assertThrows(HttpClientErrorException.class, ()-> {
            template.exchange(fullUrl(TRIPS_URL), HttpMethod.POST,
                    new HttpEntity<>(tripDtoRequest, loginClient()), String.class);
        });
        Error error = new ObjectMapper().readValue(exc.getResponseBodyAsString(), ErrorResponse.class).getErrors().get(0);
        assertAll(() -> assertEquals(exc.getRawStatusCode(), 400),
                () -> assertEquals(error.getField(), "sessionId"),
                () -> assertEquals(error.getErrorCode(), "NO_PERMISSION"));
    }

    @Test
    public void testPostTripBusNotFound() throws JsonProcessingException {
        TripDtoRequest tripDtoRequest = createTripWithDates();
        tripDtoRequest.setBusId(3);
        HttpClientErrorException exc = assertThrows(HttpClientErrorException.class, ()-> {
            template.exchange(fullUrl(TRIPS_URL), HttpMethod.POST,
                    new HttpEntity<>(tripDtoRequest, loginAdmin()), String.class);
        });
        Error error = new ObjectMapper().readValue(exc.getResponseBodyAsString(), ErrorResponse.class).getErrors().get(0);
        assertAll(() -> assertEquals(exc.getRawStatusCode(), 400),
                () -> assertEquals(error.getField(), "busId"),
                () -> assertEquals(error.getErrorCode(), "BUS_NOT_FOUND"));
    }

    @Test
    public void testPostTripWithDatesOk() throws JsonProcessingException {
        TripDtoRequest tripDtoRequest = createTripWithDates();
        ResponseEntity<String> response1 = template.exchange(fullUrl(TRIPS_URL), HttpMethod.POST,
                new HttpEntity<>(tripDtoRequest, loginAdmin()), String.class);
        TripDtoResponse tripDtoResponse = mapper.readValue(response1.getBody(), TripDtoResponse.class);
        assertAll(() -> assertEquals(response1.getStatusCodeValue(), 200),
                () -> assertEquals(tripDtoResponse.getDates().size(), tripDtoRequest.getDates().size()));
    }

    @Test
    public void testPostTripWithScheduleDailyOk() throws JsonProcessingException {
        TripDtoRequest tripDtoRequest = createTripWithSchedule("daily");
        ResponseEntity<String> response1 = template.exchange(fullUrl(TRIPS_URL), HttpMethod.POST,
                new HttpEntity<>(tripDtoRequest, loginAdmin()), String.class);
        TripDtoResponse tripDtoResponse = mapper.readValue(response1.getBody(), TripDtoResponse.class);
        assertAll(() -> assertEquals(response1.getStatusCodeValue(), 200),
                () -> assertEquals(tripDtoResponse.getDates().size(), 31));
    }

    @Test
    public void testPostTripWithScheduleOddOk() throws JsonProcessingException {
        TripDtoRequest tripDtoRequest = createTripWithSchedule("odd");
        ResponseEntity<String> response1 = template.exchange(fullUrl(TRIPS_URL), HttpMethod.POST,
                new HttpEntity<>(tripDtoRequest, loginAdmin()), String.class);
        TripDtoResponse tripDtoResponse = mapper.readValue(response1.getBody(), TripDtoResponse.class);
        assertAll(() -> assertEquals(response1.getStatusCodeValue(), 200),
                () -> assertEquals(tripDtoResponse.getDates().size(), 16));
    }

    @Test
    public void testPostTripWithScheduleEvenOk() throws JsonProcessingException {
        TripDtoRequest tripDtoRequest = createTripWithSchedule("even");
        ResponseEntity<String> response1 = template.exchange(fullUrl(TRIPS_URL), HttpMethod.POST,
                new HttpEntity<>(tripDtoRequest, loginAdmin()), String.class);
        TripDtoResponse tripDtoResponse = mapper.readValue(response1.getBody(), TripDtoResponse.class);
        assertAll(() -> assertEquals(response1.getStatusCodeValue(), 200),
                () -> assertEquals(tripDtoResponse.getDates().size(), 15));
    }

    @Test
    public void testPostTripWithScheduleDaysOfWeekOk() throws JsonProcessingException {
        TripDtoRequest tripDtoRequest = createTripWithSchedule("Mon,Tue,Sat");
        ResponseEntity<String> response1 = template.exchange(fullUrl(TRIPS_URL), HttpMethod.POST,
                new HttpEntity<>(tripDtoRequest, loginAdmin()), String.class);
        TripDtoResponse tripDtoResponse = mapper.readValue(response1.getBody(), TripDtoResponse.class);
        assertAll(() -> assertEquals(response1.getStatusCodeValue(), 200),
                () -> assertEquals(tripDtoResponse.getDates().size(), 13));
    }

    @Test
    public void testPostTripWithScheduleDaysOfMonthOk() throws JsonProcessingException {
        TripDtoRequest tripDtoRequest = createTripWithSchedule("1,2,3,6,7,9,15,20,30");
        ResponseEntity<String> response1 = template.exchange(fullUrl(TRIPS_URL), HttpMethod.POST,
                new HttpEntity<>(tripDtoRequest, loginAdmin()), String.class);
        TripDtoResponse tripDtoResponse = mapper.readValue(response1.getBody(), TripDtoResponse.class);
        assertAll(() -> assertEquals(response1.getStatusCodeValue(), 200),
                () -> assertEquals(tripDtoResponse.getDates().size(), 10));
    }

    @Test
    public void testUpdateNotExistingTrip() throws JsonProcessingException {
        TripDtoRequest tripDtoRequest = createTripWithDates();
        HttpClientErrorException exc = assertThrows(HttpClientErrorException.class, ()-> {
            template.exchange(fullUrl(TRIPS_URL) + "/1", HttpMethod.PUT,
                    new HttpEntity<>(tripDtoRequest, loginAdmin()), String.class);
        });
        Error error = new ObjectMapper().readValue(exc.getResponseBodyAsString(), ErrorResponse.class).getErrors().get(0);
        assertAll(() -> assertEquals(exc.getRawStatusCode(), 400),
                () -> assertEquals(error.getField(), "id"),
                () -> assertEquals(error.getErrorCode(), "TRIP_NOT_FOUND"));
    }

    @Test
    public void testDeleteNotExistingTrip() throws JsonProcessingException {
        HttpClientErrorException exc = assertThrows(HttpClientErrorException.class, ()-> {
            template.exchange(fullUrl(TRIPS_URL) + "/1", HttpMethod.DELETE,
                    new HttpEntity<>(loginAdmin()), String.class);
        });
        Error error = new ObjectMapper().readValue(exc.getResponseBodyAsString(), ErrorResponse.class).getErrors().get(0);
        assertAll(() -> assertEquals(exc.getRawStatusCode(), 400),
                () -> assertEquals(error.getField(), "id"),
                () -> assertEquals(error.getErrorCode(), "TRIP_NOT_FOUND"));
    }

    @Test
    public void testGetNotExistingTrip() throws JsonProcessingException {
        HttpClientErrorException exc = assertThrows(HttpClientErrorException.class, ()-> {
            template.exchange(fullUrl(TRIPS_URL) + "/1", HttpMethod.GET,
                    new HttpEntity<>(loginAdmin()), String.class);
        });
        Error error = new ObjectMapper().readValue(exc.getResponseBodyAsString(), ErrorResponse.class).getErrors().get(0);
        assertAll(() -> assertEquals(exc.getRawStatusCode(), 400),
                () -> assertEquals(error.getField(), "id"),
                () -> assertEquals(error.getErrorCode(), "TRIP_NOT_FOUND"));
    }

    @Test
    public void testUpdateTripOk() throws JsonProcessingException {
        HttpHeaders headers = loginAdmin();
        TripDtoRequest tripDtoRequest = createTripWithDates();
        ResponseEntity<String> response1 = template.exchange(fullUrl(TRIPS_URL), HttpMethod.POST,
                new HttpEntity<>(tripDtoRequest, headers), String.class);
        int id = mapper.readValue(response1.getBody(), TripDtoResponse.class).getId();
        tripDtoRequest.setDates(List.of(createDate(4, 1), createDate(4, 3),
                createDate(4, 5), createDate(4, 8)));
        tripDtoRequest.setPrice(BigDecimal.valueOf(1300.00));
        ResponseEntity<String> response2 = template.exchange(fullUrl(TRIPS_URL) + "/" + id, HttpMethod.PUT,
                new HttpEntity<>(tripDtoRequest, headers), String.class);
        TripDtoResponse tripDtoResponse = mapper.readValue(response2.getBody(), TripDtoResponse.class);
        assertAll(() -> assertEquals(response2.getStatusCodeValue(), 200),
                () -> assertEquals(tripDtoResponse.getDates().size(), tripDtoRequest.getDates().size()),
                () -> assertEquals(tripDtoResponse.getPrice(), BigDecimal.valueOf(1300.00)));
    }

    @Test
    public void testApproveTripOk() throws JsonProcessingException {
        HttpHeaders headers = loginAdmin();
        TripDtoRequest tripDtoRequest = createTripWithDates();
        ResponseEntity<String> response1 = template.exchange(fullUrl(TRIPS_URL), HttpMethod.POST,
                new HttpEntity<>(tripDtoRequest, headers), String.class);
        int id = mapper.readValue(response1.getBody(), TripDtoResponse.class).getId();
        ResponseEntity<String> response2 = template.exchange(fullUrl(TRIPS_URL) + "/" + id + "/approve",
                HttpMethod.PUT, new HttpEntity<>(headers), String.class);
        TripDtoResponse tripDtoResponse = mapper.readValue(response2.getBody(), TripDtoResponse.class);
        assertAll(() -> assertEquals(response2.getStatusCodeValue(), 200),
                () -> assertTrue(tripDtoResponse.isApproved()));
    }

    @Test
    public void testGetTripOk() throws JsonProcessingException {
        HttpHeaders headers = loginAdmin();
        TripDtoRequest tripDtoRequest = createTripWithDates();
        ResponseEntity<String> response1 = template.exchange(fullUrl(TRIPS_URL), HttpMethod.POST,
                new HttpEntity<>(tripDtoRequest, headers), String.class);
        int id = mapper.readValue(response1.getBody(), TripDtoResponse.class).getId();
        ResponseEntity<String> response2 = template.exchange(fullUrl(TRIPS_URL) + "/" + id,
                HttpMethod.GET, new HttpEntity<>(headers), String.class);
        TripDtoResponse tripDtoResponse = mapper.readValue(response2.getBody(), TripDtoResponse.class);
        assertAll(() -> assertEquals(response2.getStatusCodeValue(), 200),
                () -> assertEquals(tripDtoResponse.getId(), id),
                () -> assertEquals(tripDtoResponse.getDates().size(), tripDtoRequest.getDates().size()));
    }

    @Test
    public void testDeleteTripOk() throws JsonProcessingException {
        HttpHeaders headers = loginAdmin();
        TripDtoRequest tripDtoRequest = createTripWithDates();
        ResponseEntity<String> response1 = template.exchange(fullUrl(TRIPS_URL), HttpMethod.POST,
                new HttpEntity<>(tripDtoRequest, headers), String.class);
        int id = mapper.readValue(response1.getBody(), TripDtoResponse.class).getId();
        ResponseEntity<String> response2 = template.exchange(fullUrl(TRIPS_URL) + "/" + id,
                HttpMethod.DELETE, new HttpEntity<>(headers), String.class);
        assertEquals(response2.getStatusCodeValue(), 200);
    }

    @Test
    public void testEditApprovedTrip() throws JsonProcessingException {
        HttpHeaders headers = loginAdmin();
        TripDtoRequest tripDtoRequest = createTripWithDates();
        ResponseEntity<String> response1 = template.exchange(fullUrl(TRIPS_URL), HttpMethod.POST,
                new HttpEntity<>(tripDtoRequest, headers), String.class);
        int id = mapper.readValue(response1.getBody(), TripDtoResponse.class).getId();
        template.exchange(fullUrl(TRIPS_URL) + "/" + id + "/approve", HttpMethod.PUT,
                new HttpEntity<>(headers), String.class);
        HttpClientErrorException exc = assertThrows(HttpClientErrorException.class, ()-> {
            template.exchange(fullUrl(TRIPS_URL) + "/" + id, HttpMethod.PUT,
                    new HttpEntity<>(tripDtoRequest, headers), String.class);
        });
        Error error = new ObjectMapper().readValue(exc.getResponseBodyAsString(), ErrorResponse.class).getErrors().get(0);
        assertAll(() -> assertEquals(exc.getRawStatusCode(), 400),
                () -> assertEquals(error.getField(), "id"),
                () -> assertEquals(error.getErrorCode(), "TRIP_IS_NOT_EDITABLE"));
    }

    @Test
    public void testDeleteApprovedTrip() throws JsonProcessingException {
        HttpHeaders headers = loginAdmin();
        TripDtoRequest tripDtoRequest = createTripWithDates();
        ResponseEntity<String> response1 = template.exchange(fullUrl(TRIPS_URL), HttpMethod.POST,
                new HttpEntity<>(tripDtoRequest, headers), String.class);
        int id = mapper.readValue(response1.getBody(), TripDtoResponse.class).getId();
        template.exchange(fullUrl(TRIPS_URL) + "/" + id + "/approve", HttpMethod.PUT,
                new HttpEntity<>(headers), String.class);
        HttpClientErrorException exc = assertThrows(HttpClientErrorException.class, ()-> {
            template.exchange(fullUrl(TRIPS_URL) + "/" + id, HttpMethod.DELETE,
                    new HttpEntity<>(headers), String.class);
        });
        Error error = new ObjectMapper().readValue(exc.getResponseBodyAsString(), ErrorResponse.class).getErrors().get(0);
        assertAll(() -> assertEquals(exc.getRawStatusCode(), 400),
                () -> assertEquals(error.getField(), "id"),
                () -> assertEquals(error.getErrorCode(), "TRIP_IS_NOT_EDITABLE"));
    }

    private TripDtoRequest createTripWithDates() {
        TripDtoRequest dto = new TripDtoRequest();
        dto.setBusId(1);
        dto.setFromStation("Отправление");
        dto.setToStation("Прибытие");
        dto.setStart("10:00");
        dto.setDuration("03:30");
        dto.setPrice(BigDecimal.valueOf(1200.00));
        dto.setDates(List.of(createDate(4, 1), createDate(4, 3),
                createDate(4, 5)));
        return dto;
    }

    private TripDtoRequest createTripWithSchedule(String period) {
        TripDtoRequest dto = new TripDtoRequest();
        dto.setBusId(1);
        dto.setFromStation("Отправление");
        dto.setToStation("Прибытие");
        dto.setStart("10:00");
        dto.setDuration("03:30");
        dto.setPrice(BigDecimal.valueOf(1200.00));
        dto.setSchedule(createSchedule(period));
        return dto;
    }

    private TripDtoRequest createTripWithSchedule(String fromDate, String toDate, String period) {
        TripDtoRequest dto = new TripDtoRequest();
        dto.setBusId(1);
        dto.setFromStation("Отправление");
        dto.setToStation("Прибытие");
        dto.setStart("10:00");
        dto.setDuration("03:30");
        dto.setPrice(BigDecimal.valueOf(1200.00));
        dto.setSchedule(createSchedule(fromDate, toDate, period));
        return dto;
    }

    private ScheduleDtoRequest createSchedule(String period) {
        return createSchedule(createDate(4, 1), createDate(5, 1), period);
    }

    private ScheduleDtoRequest createSchedule(String fromDate, String toDate, String period) {
        ScheduleDtoRequest schedule = new ScheduleDtoRequest();
        schedule.setFromDate(fromDate);
        schedule.setToDate(toDate);
        schedule.setPeriod(period);
        return schedule;
    }

}
