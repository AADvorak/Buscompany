package net.thumbtack.school.buscompany;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import net.thumbtack.school.buscompany.dto.request.LoginDtoRequest;
import net.thumbtack.school.buscompany.dto.response.AdminDtoResponse;
import net.thumbtack.school.buscompany.dto.response.ClientDtoResponse;
import net.thumbtack.school.buscompany.error.Error;
import net.thumbtack.school.buscompany.error.ErrorResponse;
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
import org.springframework.web.client.HttpClientErrorException;

import java.util.Objects;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class TestIntegrationLoginLogout extends TestIntegrationBase {

    @BeforeAll
    public void beforeAll() {
        clearAll();
        createUsersInDatabase();
    }

    @Test
    public void testLoginClientEmptyLogin() throws JsonProcessingException {
        LoginDtoRequest loginDtoRequest = new LoginDtoRequest();
        loginDtoRequest.setLogin("");
        loginDtoRequest.setPassword(password);
        HttpClientErrorException exc = assertThrows(HttpClientErrorException.class, () -> {
            template.postForEntity(fullUrl(SESSIONS_URL), loginDtoRequest, String.class);
        });
        Error error = new ObjectMapper().readValue(exc.getResponseBodyAsString(), ErrorResponse.class).getErrors().get(0);
        assertAll(() -> assertEquals(exc.getRawStatusCode(), 400),
                () -> assertEquals(error.getErrorCode(), "NotEmpty"),
                () -> assertEquals(error.getField(), "login"));
    }

    @Test
    public void testLoginClientEmptyPassword() throws JsonProcessingException {
        LoginDtoRequest loginDtoRequest = new LoginDtoRequest();
        loginDtoRequest.setLogin(loginClient);
        loginDtoRequest.setPassword("");
        HttpClientErrorException exc = assertThrows(HttpClientErrorException.class, () -> {
            template.postForEntity(fullUrl(SESSIONS_URL), loginDtoRequest, String.class);
        });
        Error error = new ObjectMapper().readValue(exc.getResponseBodyAsString(), ErrorResponse.class).getErrors().get(0);
        assertAll(() -> assertEquals(exc.getRawStatusCode(), 400),
                () -> assertEquals(error.getErrorCode(), "NotEmpty"),
                () -> assertEquals(error.getField(), "password"));
    }

    @Test
    public void testLoginClientWrongLogin() throws JsonProcessingException {
        LoginDtoRequest loginDtoRequest = new LoginDtoRequest();
        loginDtoRequest.setLogin("wrong");
        loginDtoRequest.setPassword(password);
        HttpClientErrorException exc = assertThrows(HttpClientErrorException.class, () -> {
            template.postForEntity(fullUrl(SESSIONS_URL), loginDtoRequest, String.class);
        });
        Error error = new ObjectMapper().readValue(exc.getResponseBodyAsString(), ErrorResponse.class).getErrors().get(0);
        assertAll(() -> assertEquals(exc.getRawStatusCode(), 400),
                () -> assertEquals(error.getErrorCode(), "WRONG_LOGIN_PASSWORD"),
                () -> assertEquals(error.getField(), "login"));
    }

    @Test
    public void testLoginClientWrongPassword() throws JsonProcessingException {
        LoginDtoRequest loginDtoRequest = new LoginDtoRequest();
        loginDtoRequest.setLogin(loginClient);
        loginDtoRequest.setPassword("wrong");
        HttpClientErrorException exc = assertThrows(HttpClientErrorException.class, () -> {
            template.postForEntity(fullUrl(SESSIONS_URL), loginDtoRequest, String.class);
        });
        Error error = new ObjectMapper().readValue(exc.getResponseBodyAsString(), ErrorResponse.class).getErrors().get(0);
        assertAll(() -> assertEquals(exc.getRawStatusCode(), 400),
                () -> assertEquals(error.getErrorCode(), "WRONG_LOGIN_PASSWORD"),
                () -> assertEquals(error.getField(), "login"));
    }

    @Test
    public void testLoginClientOkCaseInsensitive() throws JsonProcessingException {
        LoginDtoRequest loginDtoRequest = new LoginDtoRequest();
        loginDtoRequest.setLogin(loginClient.toLowerCase());
        loginDtoRequest.setPassword(password);
        ResponseEntity<String> response = template.postForEntity(fullUrl(SESSIONS_URL), loginDtoRequest, String.class);
        ClientDtoResponse clientDtoResponse = new ObjectMapper().readValue(response.getBody(), ClientDtoResponse.class);
        assertAll(() -> assertEquals(clientDtoResponse.getLogin(), loginClient));
    }

    @Test
    public void testLoginAdminOkCaseInsensitive() throws JsonProcessingException {
        LoginDtoRequest loginDtoRequest = new LoginDtoRequest();
        loginDtoRequest.setLogin(loginAdmin.toLowerCase());
        loginDtoRequest.setPassword(password);
        ResponseEntity<String> response = template.postForEntity(fullUrl(SESSIONS_URL), loginDtoRequest, String.class);
        AdminDtoResponse adminDtoResponse = new ObjectMapper().readValue(response.getBody(), AdminDtoResponse.class);
        assertAll(() -> assertEquals(adminDtoResponse.getLogin(), loginAdmin));
    }

    @Test
    public void testLogoutWrongSessionId() throws JsonProcessingException {
        HttpClientErrorException exc = assertThrows(HttpClientErrorException.class, () -> {
            template.delete(fullUrl(SESSIONS_URL));
        });
        Error error = new ObjectMapper().readValue(exc.getResponseBodyAsString(), ErrorResponse.class).getErrors().get(0);
        assertAll(() -> assertEquals(exc.getRawStatusCode(), 400),
                () -> assertEquals(error.getErrorCode(), "SESSION_NOT_FOUND"),
                () -> assertEquals(error.getField(), "sessionId"));
    }

    @Test
    public void testLogoutOk() {
        LoginDtoRequest loginDtoRequest = new LoginDtoRequest();
        loginDtoRequest.setLogin(loginAdmin.toLowerCase());
        loginDtoRequest.setPassword(password);
        ResponseEntity<String> response = template.postForEntity(fullUrl(SESSIONS_URL), loginDtoRequest, String.class);
        HttpHeaders headers = new HttpHeaders();
        headers.add("Cookie", Objects.requireNonNull(response.getHeaders().get("Set-Cookie")).get(0));
        response = template.exchange(fullUrl(SESSIONS_URL), HttpMethod.DELETE, new HttpEntity<>(headers), String.class);
        assertEquals(response.getStatusCodeValue(), 200);
    }

    @Test
    public void testIdleTimeoutExpiration() throws JsonProcessingException {
        LoginDtoRequest loginDtoRequest = new LoginDtoRequest();
        loginDtoRequest.setLogin(loginAdmin.toLowerCase());
        loginDtoRequest.setPassword(password);
        ResponseEntity<String> response = template.postForEntity(fullUrl(SESSIONS_URL), loginDtoRequest, String.class);
        HttpHeaders headers = new HttpHeaders();
        headers.add("Cookie", Objects.requireNonNull(response.getHeaders().get("Set-Cookie")).get(0));
        try {
            Thread.sleep(6000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        HttpClientErrorException exc = assertThrows(HttpClientErrorException.class, () -> {
            template.exchange(fullUrl(ACCOUNTS_URL), HttpMethod.GET,
                    new HttpEntity<>(headers), String.class);
        });
        Error error = new ObjectMapper().readValue(exc.getResponseBodyAsString(), ErrorResponse.class).getErrors().get(0);
        assertAll(() -> assertEquals(exc.getRawStatusCode(), 400),
                () -> assertEquals(error.getErrorCode(), "SESSION_NOT_FOUND"),
                () -> assertEquals(error.getField(), "sessionId"));
    }

    @Test
    public void testIdleTimeoutProlongation() {
        LoginDtoRequest loginDtoRequest = new LoginDtoRequest();
        loginDtoRequest.setLogin(loginAdmin.toLowerCase());
        loginDtoRequest.setPassword(password);
        ResponseEntity<String> response = template.postForEntity(fullUrl(SESSIONS_URL), loginDtoRequest, String.class);
        HttpHeaders headers = new HttpHeaders();
        headers.add("Cookie", Objects.requireNonNull(response.getHeaders().get("Set-Cookie")).get(0));
        for (int i = 0; i < 3; i++) {
            try {
                Thread.sleep(3000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            response = template.exchange(fullUrl(ACCOUNTS_URL), HttpMethod.GET,
                    new HttpEntity<>(headers), String.class);
        }
        assertEquals(response.getStatusCodeValue(), 200);
    }

}
