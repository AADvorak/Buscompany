package net.thumbtack.school.buscompany;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import net.thumbtack.school.buscompany.dto.response.ClientDtoResponse;
import net.thumbtack.school.buscompany.dto.response.OrderDtoResponse;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.web.util.UriComponentsBuilder;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class TestIntegrationGetOrders extends TestIntegrationBase {

    @BeforeAll
    public void beforeAll() throws JsonProcessingException {
        clearAll();
        createUsersInDatabase();
        createTripsInDatabase();
        createOrdersInDatabase();
    }

    @Test
    public void testGetOrdersAdminNoParams() throws JsonProcessingException {
        String url = UriComponentsBuilder.fromHttpUrl(fullUrl(ORDERS_URL))
                .encode().toUriString();
        ResponseEntity<String> response = template.exchange(url, HttpMethod.GET, new HttpEntity<>(loginAdmin()), String.class);
        OrderDtoResponse[] orders = mapper.readValue(response.getBody(), OrderDtoResponse[].class);
        assertAll(() -> assertEquals(response.getStatusCodeValue(), 200),
                () -> assertEquals(orders.length, 10));
    }

    @Test
    public void testGetOrdersClientNoParams() throws JsonProcessingException {
        String url = UriComponentsBuilder.fromHttpUrl(fullUrl(ORDERS_URL))
                .encode().toUriString();
        ResponseEntity<String> response = template.exchange(url, HttpMethod.GET, new HttpEntity<>(loginClient()), String.class);
        OrderDtoResponse[] orders = mapper.readValue(response.getBody(), OrderDtoResponse[].class);
        assertAll(() -> assertEquals(response.getStatusCodeValue(), 200),
                () -> assertEquals(orders.length, 5));
    }

    @Test
    public void testGetOrdersAdminClientId() throws JsonProcessingException {
        HttpHeaders headers = loginAdmin();
        ResponseEntity<String> response2 = template.exchange(fullUrl(CLIENTS_URL), HttpMethod.GET,
                new HttpEntity<>(headers), String.class);
        ClientDtoResponse[] clientDtoResponses = new ObjectMapper().readValue(response2.getBody(), ClientDtoResponse[].class);
        String url = UriComponentsBuilder.fromHttpUrl(fullUrl(ORDERS_URL))
                .queryParam("clientId", clientDtoResponses[0].getId())
                .encode().toUriString();
        ResponseEntity<String> response = template.exchange(url, HttpMethod.GET, new HttpEntity<>(headers), String.class);
        OrderDtoResponse[] orders = mapper.readValue(response.getBody(), OrderDtoResponse[].class);
        assertAll(() -> assertEquals(response.getStatusCodeValue(), 200),
                () -> assertEquals(orders.length, 5));
    }

    @Test
    public void testGetOrdersAdminBusName() throws JsonProcessingException {
        String url = UriComponentsBuilder.fromHttpUrl(fullUrl(ORDERS_URL))
                .queryParam("busName", "Man")
                .encode().toUriString();
        ResponseEntity<String> response = template.exchange(url, HttpMethod.GET, new HttpEntity<>(loginAdmin()), String.class);
        OrderDtoResponse[] orders = mapper.readValue(response.getBody(), OrderDtoResponse[].class);
        assertAll(() -> assertEquals(response.getStatusCodeValue(), 200),
                () -> assertEquals(orders.length, 4));
    }

    @Test
    public void testGetOrdersClientBusName() throws JsonProcessingException {
        String url = UriComponentsBuilder.fromHttpUrl(fullUrl(ORDERS_URL))
                .queryParam("busName", "Man")
                .encode().toUriString();
        ResponseEntity<String> response = template.exchange(url, HttpMethod.GET, new HttpEntity<>(loginClient()), String.class);
        OrderDtoResponse[] orders = mapper.readValue(response.getBody(), OrderDtoResponse[].class);
        assertAll(() -> assertEquals(response.getStatusCodeValue(), 200),
                () -> assertEquals(orders.length, 2));
    }

    @Test
    public void testGetOrdersAdminFromStation() throws JsonProcessingException {
        String url = UriComponentsBuilder.fromHttpUrl(fullUrl(ORDERS_URL))
                .queryParam("fromStation", "Саратов")
                .encode().toUriString();
        ResponseEntity<String> response = template.exchange(url, HttpMethod.GET, new HttpEntity<>(loginAdmin()), String.class);
        OrderDtoResponse[] orders = mapper.readValue(response.getBody(), OrderDtoResponse[].class);
        assertAll(() -> assertEquals(response.getStatusCodeValue(), 200),
                () -> assertEquals(orders.length, 8));
    }

    @Test
    public void testGetOrdersClientFromStation() throws JsonProcessingException {
        String url = UriComponentsBuilder.fromHttpUrl(fullUrl(ORDERS_URL))
                .queryParam("fromStation", "Саратов")
                .encode().toUriString();
        ResponseEntity<String> response = template.exchange(url, HttpMethod.GET, new HttpEntity<>(loginClient()), String.class);
        OrderDtoResponse[] orders = mapper.readValue(response.getBody(), OrderDtoResponse[].class);
        assertAll(() -> assertEquals(response.getStatusCodeValue(), 200),
                () -> assertEquals(orders.length, 4));
    }

    @Test
    public void testGetOrdersAdminToStation() throws JsonProcessingException {
        String url = UriComponentsBuilder.fromHttpUrl(fullUrl(ORDERS_URL))
                .queryParam("toStation", "Саратов")
                .encode().toUriString();
        ResponseEntity<String> response = template.exchange(url, HttpMethod.GET, new HttpEntity<>(loginAdmin()), String.class);
        OrderDtoResponse[] orders = mapper.readValue(response.getBody(), OrderDtoResponse[].class);
        assertAll(() -> assertEquals(response.getStatusCodeValue(), 200),
                () -> assertEquals(orders.length, 2));
    }

    @Test
    public void testGetOrdersClientToStation() throws JsonProcessingException {
        String url = UriComponentsBuilder.fromHttpUrl(fullUrl(ORDERS_URL))
                .queryParam("toStation", "Саратов")
                .encode().toUriString();
        ResponseEntity<String> response = template.exchange(url, HttpMethod.GET, new HttpEntity<>(loginClient()), String.class);
        OrderDtoResponse[] orders = mapper.readValue(response.getBody(), OrderDtoResponse[].class);
        assertAll(() -> assertEquals(response.getStatusCodeValue(), 200),
                () -> assertEquals(orders.length, 1));
    }

    @Test
    public void testGetOrdersAdminPeriod() throws JsonProcessingException {
        String url = UriComponentsBuilder.fromHttpUrl(fullUrl(ORDERS_URL))
                .queryParam("fromDate", createDate(3, 1))
                .queryParam("toDate", createDate(5, 1))
                .encode().toUriString();
        ResponseEntity<String> response = template.exchange(url, HttpMethod.GET, new HttpEntity<>(loginAdmin()), String.class);
        OrderDtoResponse[] orders = mapper.readValue(response.getBody(), OrderDtoResponse[].class);
        assertAll(() -> assertEquals(response.getStatusCodeValue(), 200),
                () -> assertEquals(orders.length, 8));
    }

    @Test
    public void testGetOrdersClientPeriod() throws JsonProcessingException {
        String url = UriComponentsBuilder.fromHttpUrl(fullUrl(ORDERS_URL))
                .queryParam("fromDate", createDate(3, 1))
                .queryParam("toDate", createDate(5, 1))
                .encode().toUriString();
        ResponseEntity<String> response = template.exchange(url, HttpMethod.GET, new HttpEntity<>(loginClient()), String.class);
        OrderDtoResponse[] orders = mapper.readValue(response.getBody(), OrderDtoResponse[].class);
        assertAll(() -> assertEquals(response.getStatusCodeValue(), 200),
                () -> assertEquals(orders.length, 4));
    }

    @Test
    public void testGetOrdersClientAllParamsMatch() throws JsonProcessingException {
        String url = UriComponentsBuilder.fromHttpUrl(fullUrl(ORDERS_URL))
                .queryParam("fromDate", createDate(3, 1))
                .queryParam("toDate", createDate(5, 1))
                .queryParam("fromStation", "Саратов")
                .queryParam("toStation", "Вольск")
                .queryParam("busName", "Man")
                .queryParam("clientId", 100)
                .encode().toUriString();
        ResponseEntity<String> response = template.exchange(url, HttpMethod.GET, new HttpEntity<>(loginClient()), String.class);
        OrderDtoResponse[] orders = mapper.readValue(response.getBody(), OrderDtoResponse[].class);
        assertAll(() -> assertEquals(response.getStatusCodeValue(), 200),
                () -> assertEquals(orders.length, 1));
    }

    @Test
    public void testGetOrdersClientAllParamsNoMatch() throws JsonProcessingException {
        String url = UriComponentsBuilder.fromHttpUrl(fullUrl(ORDERS_URL))
                .queryParam("fromDate", createDate(3, 1))
                .queryParam("toDate", createDate(5, 1))
                .queryParam("fromStation", "Саратов")
                .queryParam("toStation", "Вольск")
                .queryParam("busName", "Mercedes")
                .queryParam("clientId", 100)
                .encode().toUriString();
        ResponseEntity<String> response = template.exchange(url, HttpMethod.GET, new HttpEntity<>(loginClient()), String.class);
        OrderDtoResponse[] orders = mapper.readValue(response.getBody(), OrderDtoResponse[].class);
        assertAll(() -> assertEquals(response.getStatusCodeValue(), 200),
                () -> assertEquals(orders.length, 0));
    }

    @Test
    public void testGetOrdersAdminAllParamsMatch() throws JsonProcessingException {
        HttpHeaders headers = loginAdmin();
        ResponseEntity<String> response2 = template.exchange(fullUrl(CLIENTS_URL), HttpMethod.GET,
                new HttpEntity<>(headers), String.class);
        ClientDtoResponse[] clientDtoResponses = new ObjectMapper().readValue(response2.getBody(), ClientDtoResponse[].class);
        String url = UriComponentsBuilder.fromHttpUrl(fullUrl(ORDERS_URL))
                .queryParam("fromDate", createDate(3, 1))
                .queryParam("toDate", createDate(5, 1))
                .queryParam("fromStation", "Саратов")
                .queryParam("toStation", "Вольск")
                .queryParam("busName", "Man")
                .queryParam("clientId", clientDtoResponses[0].getId())
                .encode().toUriString();
        ResponseEntity<String> response = template.exchange(url, HttpMethod.GET, new HttpEntity<>(headers), String.class);
        OrderDtoResponse[] orders = mapper.readValue(response.getBody(), OrderDtoResponse[].class);
        assertAll(() -> assertEquals(response.getStatusCodeValue(), 200),
                () -> assertEquals(orders.length, 1));
    }

    @Test
    public void testGetOrdersAdminAllParamsNoMatch() throws JsonProcessingException {
        HttpHeaders headers = loginAdmin();
        ResponseEntity<String> response2 = template.exchange(fullUrl(CLIENTS_URL), HttpMethod.GET,
                new HttpEntity<>(headers), String.class);
        ClientDtoResponse[] clientDtoResponses = new ObjectMapper().readValue(response2.getBody(), ClientDtoResponse[].class);
        String url = UriComponentsBuilder.fromHttpUrl(fullUrl(ORDERS_URL))
                .queryParam("fromDate", createDate(3, 1))
                .queryParam("toDate", createDate(5, 1))
                .queryParam("fromStation", "Саратов")
                .queryParam("toStation", "Вольск")
                .queryParam("busName", "Mercedes")
                .queryParam("clientId", clientDtoResponses[0].getId())
                .encode().toUriString();
        ResponseEntity<String> response = template.exchange(url, HttpMethod.GET, new HttpEntity<>(headers), String.class);
        OrderDtoResponse[] orders = mapper.readValue(response.getBody(), OrderDtoResponse[].class);
        assertAll(() -> assertEquals(response.getStatusCodeValue(), 200),
                () -> assertEquals(orders.length, 0));
    }

}
