package net.thumbtack.school.buscompany;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import net.thumbtack.school.buscompany.dto.request.AdminDtoRequest;
import net.thumbtack.school.buscompany.dto.request.AdminEditDtoRequest;
import net.thumbtack.school.buscompany.dto.request.ClientDtoRequest;
import net.thumbtack.school.buscompany.dto.request.ClientEditDtoRequest;
import net.thumbtack.school.buscompany.dto.response.AdminDtoResponse;
import net.thumbtack.school.buscompany.dto.response.ClientDtoResponse;
import net.thumbtack.school.buscompany.error.Error;
import net.thumbtack.school.buscompany.error.ErrorResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.web.client.HttpClientErrorException;

import java.util.Objects;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
public class TestIntegrationManageUsers extends TestIntegrationBase {

    @BeforeEach
    public void clearAllUsers() {
        userRepository.deleteAll();
    }

    @Test
    public void testDeleteAccountOk() {
        AdminDtoRequest adminDtoRequest = createAdmin();
        ResponseEntity<String> response = template.postForEntity(fullUrl(ADMINS_URL), adminDtoRequest, String.class);
        HttpHeaders headers = new HttpHeaders();
        headers.add("Cookie", Objects.requireNonNull(response.getHeaders().get("Set-Cookie")).get(0));
        response = template.exchange(fullUrl(ACCOUNTS_URL), HttpMethod.DELETE, new HttpEntity<>(headers), String.class);
        assertEquals(response.getStatusCodeValue(), 200);
    }

    @Test
    public void testGetAccountOk() throws JsonProcessingException {
        ClientDtoRequest clientDtoRequest = createClient();
        ResponseEntity<String> response = template.postForEntity(fullUrl(CLIENTS_URL), clientDtoRequest, String.class);
        HttpHeaders headers = new HttpHeaders();
        headers.add("Cookie", Objects.requireNonNull(response.getHeaders().get("Set-Cookie")).get(0));
        ResponseEntity<String> response2 = template.exchange(fullUrl(ACCOUNTS_URL), HttpMethod.GET, new HttpEntity<>(headers), String.class);
        ClientDtoResponse clientDtoResponse = new ObjectMapper().readValue(response2.getBody(), ClientDtoResponse.class);
        assertAll(() -> assertEquals(response2.getStatusCodeValue(), 200),
                () -> assertEquals(clientDtoRequest.getLogin(), clientDtoResponse.getLogin()));
    }

    @Test
    public void testEditClientNoPermission() throws JsonProcessingException {
        AdminDtoRequest adminDtoRequest = createAdmin();
        ResponseEntity<String> response = template.postForEntity(fullUrl(ADMINS_URL), adminDtoRequest, String.class);
        HttpHeaders headers = new HttpHeaders();
        headers.add("Cookie", Objects.requireNonNull(response.getHeaders().get("Set-Cookie")).get(0));
        ClientEditDtoRequest clientEditDtoRequest = createClientEdit(createClient());
        HttpClientErrorException exc = assertThrows(HttpClientErrorException.class, () -> {
            template.exchange(fullUrl(CLIENTS_URL), HttpMethod.PUT, new HttpEntity<>(clientEditDtoRequest, headers), String.class);
        });
        Error error = new ObjectMapper().readValue(exc.getResponseBodyAsString(), ErrorResponse.class).getErrors().get(0);
        assertAll(() -> assertEquals(exc.getRawStatusCode(), 400),
                () -> assertEquals(error.getField(), "sessionId"),
                () -> assertEquals(error.getErrorCode(), "NO_PERMISSION"));
    }

    @Test
    public void testEditClientWrongOldPassword() throws JsonProcessingException {
        ClientDtoRequest clientDtoRequest = createClient();
        ResponseEntity<String> response = template.postForEntity(fullUrl(CLIENTS_URL), clientDtoRequest, String.class);
        HttpHeaders headers = new HttpHeaders();
        headers.add("Cookie", Objects.requireNonNull(response.getHeaders().get("Set-Cookie")).get(0));
        ClientEditDtoRequest clientEditDtoRequest = createClientEdit(clientDtoRequest);
        clientEditDtoRequest.setOldPassword("wrong-old-password");
        HttpClientErrorException exc = assertThrows(HttpClientErrorException.class, () -> {
            template.exchange(fullUrl(CLIENTS_URL), HttpMethod.PUT, new HttpEntity<>(clientEditDtoRequest, headers), String.class);
        });
        Error error = new ObjectMapper().readValue(exc.getResponseBodyAsString(), ErrorResponse.class).getErrors().get(0);
        assertAll(() -> assertEquals(exc.getRawStatusCode(), 400),
                () -> assertEquals(error.getField(), "oldPassword"),
                () -> assertEquals(error.getErrorCode(), "WRONG_OLD_PASSWORD"));
    }

    @Test
    public void testEditClientOk() throws JsonProcessingException {
        ClientDtoRequest clientDtoRequest = createClient();
        ResponseEntity<String> response = template.postForEntity(fullUrl(CLIENTS_URL), clientDtoRequest, String.class);
        HttpHeaders headers = new HttpHeaders();
        headers.add("Cookie", Objects.requireNonNull(response.getHeaders().get("Set-Cookie")).get(0));
        ClientEditDtoRequest clientEditDtoRequest = createClientEdit(clientDtoRequest);
        ResponseEntity<String> response2 = template.exchange(fullUrl(CLIENTS_URL), HttpMethod.PUT,
                new HttpEntity<>(clientEditDtoRequest, headers), String.class);
        ClientDtoResponse clientDtoResponse = new ObjectMapper().readValue(response2.getBody(), ClientDtoResponse.class);
        assertAll(() -> assertEquals(response2.getStatusCodeValue(), 200),
                () -> assertEquals(clientEditDtoRequest.getFirstName(), clientDtoResponse.getFirstName()),
                () -> assertEquals(clientEditDtoRequest.getLastName(), clientDtoResponse.getLastName()),
                () -> assertEquals(clientEditDtoRequest.getPatronymic(), clientDtoResponse.getPatronymic()),
                () -> assertEquals(clientEditDtoRequest.getEmail(), clientDtoResponse.getEmail()),
                () -> assertEquals("+7" + clientEditDtoRequest.getPhone(), clientDtoResponse.getPhone()));
    }

    @Test
    public void testEditAdminOk() throws JsonProcessingException {
        AdminDtoRequest adminDtoRequest = createAdmin();
        ResponseEntity<String> response = template.postForEntity(fullUrl(ADMINS_URL), adminDtoRequest, String.class);
        HttpHeaders headers = new HttpHeaders();
        headers.add("Cookie", Objects.requireNonNull(response.getHeaders().get("Set-Cookie")).get(0));
        AdminEditDtoRequest adminEditDtoRequest = createAdminEdit(adminDtoRequest);
        ResponseEntity<String> response2 = template.exchange(fullUrl(ADMINS_URL), HttpMethod.PUT,
                new HttpEntity<>(adminEditDtoRequest, headers), String.class);
        AdminDtoResponse adminDtoResponse = new ObjectMapper().readValue(response2.getBody(), AdminDtoResponse.class);
        assertAll(() -> assertEquals(response2.getStatusCodeValue(), 200),
                () -> assertEquals(adminEditDtoRequest.getFirstName(), adminDtoResponse.getFirstName()),
                () -> assertEquals(adminEditDtoRequest.getLastName(), adminDtoResponse.getLastName()),
                () -> assertEquals(adminEditDtoRequest.getPatronymic(), adminDtoResponse.getPatronymic()),
                () -> assertEquals(adminEditDtoRequest.getPosition(), adminDtoResponse.getPosition()));
    }

    @Test
    public void testGetClientsNoPermission() throws JsonProcessingException {
        ClientDtoRequest clientDtoRequest = createClient();
        ResponseEntity<String> response = template.postForEntity(fullUrl(CLIENTS_URL), clientDtoRequest, String.class);
        HttpHeaders headers = new HttpHeaders();
        headers.add("Cookie", Objects.requireNonNull(response.getHeaders().get("Set-Cookie")).get(0));
        HttpClientErrorException exc = assertThrows(HttpClientErrorException.class, () -> {
            template.exchange(fullUrl(CLIENTS_URL), HttpMethod.GET, new HttpEntity<>(headers), String.class);
        });
        Error error = new ObjectMapper().readValue(exc.getResponseBodyAsString(), ErrorResponse.class).getErrors().get(0);
        assertAll(() -> assertEquals(exc.getRawStatusCode(), 400),
                () -> assertEquals(error.getField(), "sessionId"),
                () -> assertEquals(error.getErrorCode(), "NO_PERMISSION"));
    }

    @Test
    public void testGetClientsOk() throws JsonProcessingException {
        ClientDtoRequest clientDtoRequest = createClient();
        template.postForEntity(fullUrl(CLIENTS_URL), clientDtoRequest, String.class);
        clientDtoRequest.setLogin(clientDtoRequest.getLogin() + "1");
        template.postForEntity(fullUrl(CLIENTS_URL), clientDtoRequest, String.class);
        AdminDtoRequest adminDtoRequest = createAdmin();
        ResponseEntity<String> response = template.postForEntity(fullUrl(ADMINS_URL), adminDtoRequest, String.class);
        HttpHeaders headers = new HttpHeaders();
        headers.add("Cookie", Objects.requireNonNull(response.getHeaders().get("Set-Cookie")).get(0));
        ResponseEntity<String> response2 =
                template.exchange(fullUrl(CLIENTS_URL), HttpMethod.GET, new HttpEntity<>(headers), String.class);
        ClientDtoResponse[] clientDtoResponses = new ObjectMapper().readValue(response2.getBody(), ClientDtoResponse[].class);
        assertAll(() -> assertEquals(response2.getStatusCodeValue(), 200),
                () -> assertEquals(clientDtoResponses.length, 2));
    }

    private AdminDtoRequest createAdmin() {
        AdminDtoRequest dto = new AdminDtoRequest();
        dto.setPosition("position");
        dto.setFirstName("Ва-лидное Имя");
        dto.setLastName("Ва-лидная Фамилия");
        dto.setPatronymic("Ва-лидное Отчество");
        dto.setLogin("LogИn2");
        dto.setPassword("password0000");
        return dto;
    }

    private ClientDtoRequest createClient() {
        ClientDtoRequest dto = new ClientDtoRequest();
        dto.setFirstName("Ва-лидное Имя");
        dto.setLastName("Ва-лидная Фамилия");
        dto.setPatronymic("Ва-лидное Отчество");
        dto.setLogin("LogИn1");
        dto.setPassword("password0000");
        dto.setPhone("+7-900-200-00-01");
        dto.setEmail("name@host.ru");
        return dto;
    }

    private ClientEditDtoRequest createClientEdit(ClientDtoRequest clientDtoRequest) {
        ClientEditDtoRequest dto = new ClientEditDtoRequest();
        dto.setFirstName(clientDtoRequest.getFirstName() + "-");
        dto.setLastName(clientDtoRequest.getLastName() + "-");
        dto.setPatronymic(clientDtoRequest.getPatronymic() + "-");
        dto.setEmail("1" + clientDtoRequest.getEmail());
        dto.setPhone("+7-900-200-11-11");
        dto.setOldPassword(clientDtoRequest.getPassword());
        dto.setNewPassword(clientDtoRequest.getPassword() + "1");
        return dto;
    }

    private AdminEditDtoRequest createAdminEdit(AdminDtoRequest adminDtoRequest) {
        AdminEditDtoRequest dto = new AdminEditDtoRequest();
        dto.setFirstName(adminDtoRequest.getFirstName() + "-");
        dto.setLastName(adminDtoRequest.getLastName() + "-");
        dto.setPatronymic(adminDtoRequest.getPatronymic() + "-");
        dto.setPosition(adminDtoRequest.getPosition() + "-");
        dto.setOldPassword(adminDtoRequest.getPassword());
        dto.setNewPassword(adminDtoRequest.getPassword() + "1");
        return dto;
    }

}
