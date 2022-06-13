package net.thumbtack.school.buscompany;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import net.thumbtack.school.buscompany.dto.request.AdminDtoRequest;
import net.thumbtack.school.buscompany.dto.request.ClientDtoRequest;
import net.thumbtack.school.buscompany.dto.response.AdminDtoResponse;
import net.thumbtack.school.buscompany.dto.response.ClientDtoResponse;
import net.thumbtack.school.buscompany.error.Error;
import net.thumbtack.school.buscompany.error.ErrorResponse;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.web.client.HttpClientErrorException;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
public class TestIntegrationRegisterUser extends TestIntegrationBase {

    private final char[] nameLetters = {'а', 'б', 'в', 'г', 'д'};

    @BeforeEach
    public void clearAllUsers() {
        userRepository.deleteAll();
    }

    @Test
    public void testRegisterAdminLongPosition() throws JsonProcessingException {
        AdminDtoRequest adminDtoRequest = createAdmin();
        adminDtoRequest.setPosition(RandomStringUtils.randomAlphabetic(applicationProperties.getMaxNameLength() + 1));
        HttpClientErrorException exc = assertThrows(HttpClientErrorException.class, ()-> {
            template.postForEntity(fullUrl(ADMINS_URL), adminDtoRequest, String.class);
        });
        Error error = new ObjectMapper().readValue(exc.getResponseBodyAsString(), ErrorResponse.class).getErrors().get(0);
        assertAll(() -> assertEquals(exc.getRawStatusCode(), 400),
                () -> assertEquals(error.getErrorCode(), "MaxLength"),
                () -> assertEquals(error.getField(), "position"));
    }

    @Test
    public void testRegisterAdminEmptyFirstName() throws JsonProcessingException {
        AdminDtoRequest adminDtoRequest = createAdmin();
        adminDtoRequest.setFirstName("");
        HttpClientErrorException exc = assertThrows(HttpClientErrorException.class, ()-> {
            template.postForEntity(fullUrl(ADMINS_URL), adminDtoRequest, String.class);
        });
        Error error = new ObjectMapper().readValue(exc.getResponseBodyAsString(), ErrorResponse.class).getErrors().get(0);
        assertAll(() -> assertEquals(exc.getRawStatusCode(), 400),
                () -> assertEquals(error.getErrorCode(), "NotEmpty"),
                () -> assertEquals(error.getField(), "firstName"));
    }

    @Test
    public void testRegisterAdminNotValidCharsFirstName() throws JsonProcessingException {
        AdminDtoRequest adminDtoRequest = createAdmin();
        adminDtoRequest.setFirstName("Thomas");
        HttpClientErrorException exc = assertThrows(HttpClientErrorException.class, ()-> {
            template.postForEntity(fullUrl(ADMINS_URL), adminDtoRequest, String.class);
        });
        Error error = new ObjectMapper().readValue(exc.getResponseBodyAsString(), ErrorResponse.class).getErrors().get(0);
        assertAll(() -> assertEquals(exc.getRawStatusCode(), 400),
                () -> assertEquals(error.getErrorCode(), "Pattern"),
                () -> assertEquals(error.getField(), "firstName"));
    }

    @Test
    public void testRegisterAdminLongFirstName() throws JsonProcessingException {
        AdminDtoRequest adminDtoRequest = createAdmin();
        adminDtoRequest.setFirstName(RandomStringUtils.random(applicationProperties.getMaxNameLength() + 1, nameLetters));
        HttpClientErrorException exc = assertThrows(HttpClientErrorException.class, ()-> {
            template.postForEntity(fullUrl(ADMINS_URL), adminDtoRequest, String.class);
        });
        Error error = new ObjectMapper().readValue(exc.getResponseBodyAsString(), ErrorResponse.class).getErrors().get(0);
        assertAll(() -> assertEquals(exc.getRawStatusCode(), 400),
                () -> assertEquals(error.getErrorCode(), "MaxLength"),
                () -> assertEquals(error.getField(), "firstName"));
    }

    @Test
    public void testRegisterAdminEmptyLastName() throws JsonProcessingException {
        AdminDtoRequest adminDtoRequest = createAdmin();
        adminDtoRequest.setLastName("");
        HttpClientErrorException exc = assertThrows(HttpClientErrorException.class, ()-> {
            template.postForEntity(fullUrl(ADMINS_URL), adminDtoRequest, String.class);
        });
        Error error = new ObjectMapper().readValue(exc.getResponseBodyAsString(), ErrorResponse.class).getErrors().get(0);
        assertAll(() -> assertEquals(exc.getRawStatusCode(), 400),
                () -> assertEquals(error.getErrorCode(), "NotEmpty"),
                () -> assertEquals(error.getField(), "lastName"));
    }

    @Test
    public void testRegisterAdminNotValidCharsLastName() throws JsonProcessingException {
        AdminDtoRequest adminDtoRequest = createAdmin();
        adminDtoRequest.setLastName("Thomas");
        HttpClientErrorException exc = assertThrows(HttpClientErrorException.class, ()-> {
            template.postForEntity(fullUrl(ADMINS_URL), adminDtoRequest, String.class);
        });
        Error error = new ObjectMapper().readValue(exc.getResponseBodyAsString(), ErrorResponse.class).getErrors().get(0);
        assertAll(() -> assertEquals(exc.getRawStatusCode(), 400),
                () -> assertEquals(error.getErrorCode(), "Pattern"),
                () -> assertEquals(error.getField(), "lastName"));
    }

    @Test
    public void testRegisterAdminLongLastName() throws JsonProcessingException {
        AdminDtoRequest adminDtoRequest = createAdmin();
        adminDtoRequest.setLastName(RandomStringUtils.random(applicationProperties.getMaxNameLength() + 1, nameLetters));
        HttpClientErrorException exc = assertThrows(HttpClientErrorException.class, ()-> {
            template.postForEntity(fullUrl(ADMINS_URL), adminDtoRequest, String.class);
        });
        Error error = new ObjectMapper().readValue(exc.getResponseBodyAsString(), ErrorResponse.class).getErrors().get(0);
        assertAll(() -> assertEquals(exc.getRawStatusCode(), 400),
                () -> assertEquals(error.getErrorCode(), "MaxLength"),
                () -> assertEquals(error.getField(), "lastName"));
    }

    @Test
    public void testRegisterAdminNotValidCharsPatronymic() throws JsonProcessingException {
        AdminDtoRequest adminDtoRequest = createAdmin();
        adminDtoRequest.setPatronymic("Thomas");
        HttpClientErrorException exc = assertThrows(HttpClientErrorException.class, ()-> {
            template.postForEntity(fullUrl(ADMINS_URL), adminDtoRequest, String.class);
        });
        Error error = new ObjectMapper().readValue(exc.getResponseBodyAsString(), ErrorResponse.class).getErrors().get(0);
        assertAll(() -> assertEquals(exc.getRawStatusCode(), 400),
                () -> assertEquals(error.getErrorCode(), "Pattern"),
                () -> assertEquals(error.getField(), "patronymic"));
    }

    @Test
    public void testRegisterAdminLongPatronymic() throws JsonProcessingException {
        AdminDtoRequest adminDtoRequest = createAdmin();
        adminDtoRequest.setPatronymic(RandomStringUtils.random(applicationProperties.getMaxNameLength() + 1, nameLetters));
        HttpClientErrorException exc = assertThrows(HttpClientErrorException.class, ()-> {
            template.postForEntity(fullUrl(ADMINS_URL), adminDtoRequest, String.class);
        });
        Error error = new ObjectMapper().readValue(exc.getResponseBodyAsString(), ErrorResponse.class).getErrors().get(0);
        assertAll(() -> assertEquals(exc.getRawStatusCode(), 400),
                () -> assertEquals(error.getErrorCode(), "MaxLength"),
                () -> assertEquals(error.getField(), "patronymic"));
    }

    @Test
    public void testRegisterAdminEmptyLogin() throws JsonProcessingException {
        AdminDtoRequest adminDtoRequest = createAdmin();
        adminDtoRequest.setLogin("");
        HttpClientErrorException exc = assertThrows(HttpClientErrorException.class, ()-> {
            template.postForEntity(fullUrl(ADMINS_URL), adminDtoRequest, String.class);
        });
        Error error = new ObjectMapper().readValue(exc.getResponseBodyAsString(), ErrorResponse.class).getErrors().get(0);
        assertAll(() -> assertEquals(exc.getRawStatusCode(), 400),
                () -> assertEquals(error.getErrorCode(), "NotEmpty"),
                () -> assertEquals(error.getField(), "login"));
    }

    @Test
    public void testRegisterAdminNotValidCharsLogin() throws JsonProcessingException {
        AdminDtoRequest adminDtoRequest = createAdmin();
        adminDtoRequest.setLogin("LogИn1-");
        HttpClientErrorException exc = assertThrows(HttpClientErrorException.class, ()-> {
            template.postForEntity(fullUrl(ADMINS_URL), adminDtoRequest, String.class);
        });
        Error error = new ObjectMapper().readValue(exc.getResponseBodyAsString(), ErrorResponse.class).getErrors().get(0);
        assertAll(() -> assertEquals(exc.getRawStatusCode(), 400),
                () -> assertEquals(error.getErrorCode(), "Pattern"),
                () -> assertEquals(error.getField(), "login"));
    }

    @Test
    public void testRegisterAdminLongLogin() throws JsonProcessingException {
        AdminDtoRequest adminDtoRequest = createAdmin();
        adminDtoRequest.setLogin(RandomStringUtils.randomAlphabetic(applicationProperties.getMaxNameLength() + 1));
        HttpClientErrorException exc = assertThrows(HttpClientErrorException.class, ()-> {
            template.postForEntity(fullUrl(ADMINS_URL), adminDtoRequest, String.class);
        });
        Error error = new ObjectMapper().readValue(exc.getResponseBodyAsString(), ErrorResponse.class).getErrors().get(0);
        assertAll(() -> assertEquals(exc.getRawStatusCode(), 400),
                () -> assertEquals(error.getErrorCode(), "MaxLength"),
                () -> assertEquals(error.getField(), "login"));
    }

    @Test
    public void testRegisterAdminLongPassword() throws JsonProcessingException {
        AdminDtoRequest adminDtoRequest = createAdmin();
        adminDtoRequest.setPassword(RandomStringUtils.randomAlphabetic(applicationProperties.getMaxNameLength() + 1));
        HttpClientErrorException exc = assertThrows(HttpClientErrorException.class, ()-> {
            template.postForEntity(fullUrl(ADMINS_URL), adminDtoRequest, String.class);
        });
        Error error = new ObjectMapper().readValue(exc.getResponseBodyAsString(), ErrorResponse.class).getErrors().get(0);
        assertAll(() -> assertEquals(exc.getRawStatusCode(), 400),
                () -> assertEquals(error.getErrorCode(), "MaxLength"),
                () -> assertEquals(error.getField(), "password"));
    }

    @Test
    public void testRegisterAdminShortPassword() throws JsonProcessingException {
        AdminDtoRequest adminDtoRequest = createAdmin();
        adminDtoRequest.setPassword(RandomStringUtils.randomAlphabetic(applicationProperties.getMinPasswordLength() - 1));
        HttpClientErrorException exc = assertThrows(HttpClientErrorException.class, ()-> {
            template.postForEntity(fullUrl(ADMINS_URL), adminDtoRequest, String.class);
        });
        Error error = new ObjectMapper().readValue(exc.getResponseBodyAsString(), ErrorResponse.class).getErrors().get(0);
        assertAll(() -> assertEquals(exc.getRawStatusCode(), 400),
                () -> assertEquals(error.getErrorCode(), "MinLength"),
                () -> assertEquals(error.getField(), "password"));
    }

    @Test
    public void testRegisterAdminOkEmptyPatronymic() {
        AdminDtoRequest adminDtoRequest = createAdmin();
        adminDtoRequest.setPatronymic("");
        AdminDtoResponse adminDtoResponse = template.postForObject(fullUrl(ADMINS_URL),
                adminDtoRequest, AdminDtoResponse.class);
        assert adminDtoResponse != null;
        assertAll(() -> assertEquals(adminDtoRequest.getLogin(), adminDtoResponse.getLogin()));
    }

    @Test
    public void testRegisterAdminOk() {
        AdminDtoRequest adminDtoRequest = createAdmin();
        AdminDtoResponse adminDtoResponse = template.postForObject(fullUrl(ADMINS_URL),
                adminDtoRequest, AdminDtoResponse.class);
        assert adminDtoResponse != null;
        assertAll(() -> assertEquals(adminDtoRequest.getLogin(), adminDtoResponse.getLogin()));
    }

    @Test
    public void testRegisterAdminExistingLoginCaseInsensitive() throws JsonProcessingException {
        AdminDtoRequest adminDtoRequest = createAdmin();
        template.postForObject(fullUrl(ADMINS_URL), adminDtoRequest, AdminDtoResponse.class);
        adminDtoRequest.setLogin(adminDtoRequest.getLogin().toLowerCase());
        HttpClientErrorException exc = assertThrows(HttpClientErrorException.class, ()-> {
            template.postForEntity(fullUrl(ADMINS_URL), adminDtoRequest, String.class);
        });
        Error error = new ObjectMapper().readValue(exc.getResponseBodyAsString(), ErrorResponse.class).getErrors().get(0);
        assertAll(() -> assertEquals(exc.getRawStatusCode(), 400),
                () -> assertEquals(error.getErrorCode(), "LOGIN_ALREADY_EXIST"),
                () -> assertEquals(error.getField(), "login"));
    }

    @Test
    public void testRegisterClientNotValidEmail() throws JsonProcessingException {
        ClientDtoRequest clientDtoRequest = createClient();
        clientDtoRequest.setEmail("email");
        HttpClientErrorException exc = assertThrows(HttpClientErrorException.class, ()-> {
            template.postForEntity(fullUrl(CLIENTS_URL), clientDtoRequest, String.class);
        });
        Error error = new ObjectMapper().readValue(exc.getResponseBodyAsString(), ErrorResponse.class).getErrors().get(0);
        assertAll(() -> assertEquals(exc.getRawStatusCode(), 400),
                () -> assertEquals(error.getErrorCode(), "Email"),
                () -> assertEquals(error.getField(), "email"));
    }

    /*@Test
    public void testRegisterClientLongEmail() throws JsonProcessingException {
        ClientDtoRequest clientDtoRequest = createClient();
        clientDtoRequest.setEmail(RandomStringUtils.randomAlphabetic(applicationProperties.getMaxNameLength()) + "@mail.ru");
        HttpClientErrorException exc = assertThrows(HttpClientErrorException.class, ()-> {
            template.postForEntity(fullUrl(CLIENTS_URL), clientDtoRequest, String.class);
        });
        Error error = new ObjectMapper().readValue(exc.getResponseBodyAsString(), ErrorResponse.class).getErrors().get(0);
        assertAll(() -> assertEquals(exc.getRawStatusCode(), 400),
                () -> assertEquals(error.getErrorCode(), "MaxLength"),
                () -> assertEquals(error.getField(), "email"));
    }*/

    @Test
    public void testRegisterClientNotValidPhone() throws JsonProcessingException {
        ClientDtoRequest clientDtoRequest = createClient();
        clientDtoRequest.setPhone("88-99-11");
        HttpClientErrorException exc = assertThrows(HttpClientErrorException.class, ()-> {
            template.postForEntity(fullUrl(CLIENTS_URL), clientDtoRequest, String.class);
        });
        Error error = new ObjectMapper().readValue(exc.getResponseBodyAsString(), ErrorResponse.class).getErrors().get(0);
        assertAll(() -> assertEquals(exc.getRawStatusCode(), 400),
                () -> assertEquals(error.getErrorCode(), "Phone"),
                () -> assertEquals(error.getField(), "phone"));
    }

    @Test
    public void testRegisterClientOk() {
        ClientDtoRequest clientDtoRequest = createClient();
        ClientDtoResponse clientDtoResponse = template.postForObject(fullUrl(CLIENTS_URL),
                clientDtoRequest, ClientDtoResponse.class);
        assert clientDtoResponse != null;
        assertAll(() -> assertEquals(clientDtoRequest.getLogin(), clientDtoResponse.getLogin()));
    }

    private AdminDtoRequest createAdmin() {
        AdminDtoRequest dto = new AdminDtoRequest();
        dto.setPosition("position");
        dto.setFirstName("Ва-лидное Имя");
        dto.setLastName("Ва-лидная Фамилия");
        dto.setPatronymic("Ва-лидное Отчество");
        dto.setLogin("LogИn1");
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

}
