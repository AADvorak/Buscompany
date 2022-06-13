package net.thumbtack.school.buscompany;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JSR310Module;
import net.thumbtack.school.buscompany.dto.request.*;
import net.thumbtack.school.buscompany.dto.response.TripDtoResponse;
import net.thumbtack.school.buscompany.repository.TripRepository;
import net.thumbtack.school.buscompany.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class TestIntegrationBase {

    @Autowired
    protected ApplicationProperties applicationProperties;

    @Autowired
    protected ServerProperties serverProperties;

    protected static final String BASE_URL = "http://localhost:";
    protected static final String TRIPS_URL = "/api/trips";
    protected static final String ORDERS_URL = "/api/orders";
    protected static final String PLACES_URL = "/api/places";
    protected static final String SESSIONS_URL = "/api/sessions";
    protected static final String ADMINS_URL = "/api/admins";
    protected static final String CLIENTS_URL = "/api/clients";
    protected static final String ACCOUNTS_URL = "/api/accounts";
    protected static final String SETTINGS_URL = "/api/settings";

    protected final RestTemplate template = new RestTemplate();

    protected final String loginClient = "LogИn1";
    protected final String loginClient2 = "LogИn12";
    protected final String loginAdmin = "LogИn2";
    protected final String password = "password0000";

    protected final int YEAR = 2022;

    protected final ObjectMapper mapper = createMapperWithJavaTimeSupport();

    @Autowired
    protected UserRepository userRepository;

    @Autowired
    protected TripRepository tripRepository;

    protected void createUsersInDatabase() {
        ClientDtoRequest clientDtoRequest = new ClientDtoRequest();
        clientDtoRequest.setFirstName("Ва-лидное Имя");
        clientDtoRequest.setLastName("Ва-лидная Фамилия");
        clientDtoRequest.setPatronymic("Ва-лидное Отчество");
        clientDtoRequest.setLogin(loginClient);
        clientDtoRequest.setPassword(password);
        clientDtoRequest.setPhone("+7-900-200-00-01");
        clientDtoRequest.setEmail("name@host.ru");
        template.postForEntity(fullUrl(CLIENTS_URL), clientDtoRequest, String.class);
        clientDtoRequest.setLogin(loginClient2);
        template.postForEntity(fullUrl(CLIENTS_URL), clientDtoRequest, String.class);
        AdminDtoRequest adminDtoRequest = new AdminDtoRequest();
        adminDtoRequest.setPosition("position");
        adminDtoRequest.setFirstName("Ва-лидное Имя");
        adminDtoRequest.setLastName("Ва-лидная Фамилия");
        adminDtoRequest.setPatronymic("Ва-лидное Отчество");
        adminDtoRequest.setLogin(loginAdmin);
        adminDtoRequest.setPassword(password);
        template.postForEntity(fullUrl(ADMINS_URL), adminDtoRequest, String.class);
    }

    protected void createTripsInDatabase() throws JsonProcessingException {
        List<TripDtoRequest> tripDtoRequests = List.of(
                createTrip(1, "Саратов", "Пенза", createSchedule(4, "daily")),
                createTrip(1, "Пенза", "Саратов", createSchedule(4, "daily")),
                createTrip(2, "Саратов", "Маркс", createSchedule(4, "odd")),
                createTrip(2, "Саратов", "Вольск", createSchedule(4, "even")),
                createTrip(1, "Саратов", "Вольск", createSchedule(6, "Mon,Tue,Wed,Thu,Fri"))
        );
        HttpHeaders headers = loginAdmin();
        for (TripDtoRequest tripDtoRequest : tripDtoRequests) {
            postTrip(tripDtoRequest, headers, true);
        }
        postTrip(createTrip(1, "Саратов", "Волгоград", createSchedule(4, "daily")), headers, false);
    }

    protected void createOrdersInDatabase() throws JsonProcessingException {
        HttpHeaders headers = loginClient();
        ResponseEntity<String> response = template.exchange(fullUrl(TRIPS_URL), HttpMethod.GET,
                new HttpEntity<>(headers), String.class);
        TripDtoResponse[] tripDtoResponses = mapper.readValue(response.getBody(), TripDtoResponse[].class);
        for (TripDtoResponse tripDtoResponse : tripDtoResponses) {
            template.exchange(fullUrl(ORDERS_URL), HttpMethod.POST,
                    new HttpEntity<>(createOrderForFirstDateOfTrip(tripDtoResponse, 2), headers), String.class);
        }
        headers = login(loginClient2);
        for (TripDtoResponse tripDtoResponse : tripDtoResponses) {
            template.exchange(fullUrl(ORDERS_URL), HttpMethod.POST,
                    new HttpEntity<>(createOrderForFirstDateOfTrip(tripDtoResponse, 2), headers), String.class);
        }
    }

    protected void clearAll() {
        template.postForEntity(fullUrl("/api/debug/clear"), null, String.class);
    }

    protected void clearOrders() {
        template.postForEntity(fullUrl("/api/debug/clear/orders"), null, String.class);
    }

    protected HttpHeaders loginAdmin() {
        return login(loginAdmin);
    }

    protected HttpHeaders loginClient() {
        return login(loginClient);
    }

    protected HttpHeaders login(String login) {
        LoginDtoRequest loginDtoRequest = new LoginDtoRequest();
        loginDtoRequest.setLogin(login);
        loginDtoRequest.setPassword(password);
        ResponseEntity<String> response = template.postForEntity(fullUrl(SESSIONS_URL), loginDtoRequest, String.class);
        HttpHeaders headers = new HttpHeaders();
        headers.add("Cookie", Objects.requireNonNull(response.getHeaders().get("Set-Cookie")).get(0));
        return headers;
    }

    protected ObjectMapper createMapperWithJavaTimeSupport() {
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JSR310Module());
        return mapper;
    }

    protected void postTrip(TripDtoRequest tripDtoRequest, HttpHeaders headers, boolean approve) throws JsonProcessingException {
        ResponseEntity<String> response = template.exchange(fullUrl(TRIPS_URL), HttpMethod.POST,
                new HttpEntity<>(tripDtoRequest, headers), String.class);
        if (approve) {
            int id = mapper.readValue(response.getBody(), TripDtoResponse.class).getId();
            template.exchange(fullUrl(TRIPS_URL) + "/" + id + "/approve",
                    HttpMethod.PUT, new HttpEntity<>(headers), String.class);
        }
    }

    protected TripDtoRequest createTrip(int busId, String fromStation, String toStation, ScheduleDtoRequest schedule) {
        TripDtoRequest dto = new TripDtoRequest();
        dto.setBusId(busId);
        dto.setFromStation(fromStation);
        dto.setToStation(toStation);
        dto.setStart("10:00");
        dto.setDuration("03:30");
        dto.setPrice(BigDecimal.valueOf(1200.00));
        dto.setSchedule(schedule);
        return dto;
    }

    protected ScheduleDtoRequest createSchedule(int month, String period) {
        ScheduleDtoRequest schedule = new ScheduleDtoRequest();
        schedule.setFromDate(createDate(month, 1));
        schedule.setToDate(createDate(month + 1, 1));
        schedule.setPeriod(period);
        return schedule;
    }

    protected String createDate(int month, int day) {
        return LocalDate.of(YEAR, month, day).toString();
    }

    protected OrderDtoRequest createOrderForFirstDateOfTrip(TripDtoResponse tripDtoResponse, int passengersCount) {
        return createOrder(tripDtoResponse.getId(), tripDtoResponse.getDates().get(0), passengersCount);
    }

    protected OrderDtoRequest createOrder(int tripId, String date, int passengersCount) {
        OrderDtoRequest dto = new OrderDtoRequest();
        dto.setTripId(tripId);
        dto.setDate(date);
        dto.setPassengers(createPassengers(passengersCount));
        return dto;
    }

    protected List<PassengerDtoRequest> createPassengers(int count) {
        List<PassengerDtoRequest> passengers = new ArrayList<>();
        for (int i = 1; i <= count; i++) {
            passengers.add(new PassengerDtoRequest("Имя" + i, "Фамилия" + i, "1111 11111" + i));
        }
        return passengers;
    }

    protected String fullUrl(String contextUrl) {
        return BASE_URL + serverProperties.getPort() + contextUrl;
    }
    
}
