package net.thumbtack.school.buscompany;

import com.fasterxml.jackson.core.JsonProcessingException;
import net.thumbtack.school.buscompany.dto.request.OrderDtoRequest;
import net.thumbtack.school.buscompany.error.Error;
import net.thumbtack.school.buscompany.error.ErrorResponse;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.RequestEntity;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;

import java.net.URI;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class TestIntegrationErrors extends TestIntegrationBase {

    @Test
    public void testMethodNotAllowed() {
        OrderDtoRequest orderDtoRequest = createOrder(1, createDate(4, 1), 0);
        HttpClientErrorException exc = assertThrows(HttpClientErrorException.class,
                () -> template.exchange(fullUrl(ORDERS_URL),
                        HttpMethod.PUT,
                        new HttpEntity<>(orderDtoRequest),
                        String.class));
        assertEquals(405, exc.getRawStatusCode());
    }

    @Test
    public void testWrongUrl() throws JsonProcessingException {
        HttpClientErrorException exc = assertThrows(HttpClientErrorException.class,
                () -> template.getForEntity(fullUrl(ORDERS_URL + "a"), String.class));
        Error error = mapper.readValue(exc.getResponseBodyAsString(), ErrorResponse.class).getErrors().get(0);
        assertAll(() -> assertEquals(404, exc.getRawStatusCode()),
                () -> assertEquals("NOT_FOUND", error.getErrorCode()));
    }

    @Test
    public void testJsonFormat() throws JsonProcessingException {
        HttpClientErrorException exc = assertThrows(HttpClientErrorException.class,
                () -> template.exchange(RequestEntity
                        .post(new URI(fullUrl(TRIPS_URL)))
                        .contentType(MediaType.APPLICATION_JSON)
                        .body("{busId:4}"), String.class));
        Error error = mapper.readValue(exc.getResponseBodyAsString(), ErrorResponse.class).getErrors().get(0);
        assertAll(() -> assertEquals(400, exc.getRawStatusCode()),
                () -> assertEquals("HTTP_MSG_NOT_READABLE", error.getErrorCode()));
    }

    @Test
    public void testMediaType() throws JsonProcessingException {
        HttpClientErrorException exc = assertThrows(HttpClientErrorException.class,
                () -> template.exchange(RequestEntity
                        .post(new URI(fullUrl(TRIPS_URL)))
                        .contentType(MediaType.APPLICATION_XML)
                        .body("<body></body>"), String.class));
        Error error = mapper.readValue(exc.getResponseBodyAsString(), ErrorResponse.class).getErrors().get(0);
        assertAll(() -> assertEquals(400, exc.getRawStatusCode()),
                () -> assertEquals("INVALID_MEDIA_TYPE", error.getErrorCode()));
    }

    @Test
    public void testPathVariable() throws JsonProcessingException {
        HttpClientErrorException exc = assertThrows(HttpClientErrorException.class,
                () -> template.getForEntity(fullUrl(TRIPS_URL + "/a"), String.class));
        Error error = mapper.readValue(exc.getResponseBodyAsString(), ErrorResponse.class).getErrors().get(0);
        assertAll(() -> assertEquals(400, exc.getRawStatusCode()),
                () -> assertEquals("METHOD_ARGUMENT_TYPE_MISMATCH", error.getErrorCode()));
    }

    @Test
    public void testException() throws JsonProcessingException {
        HttpServerErrorException exc = assertThrows(HttpServerErrorException.class,
                () -> template.getForEntity(fullUrl("/api/debug/exception"), String.class));
        Error error = mapper.readValue(exc.getResponseBodyAsString(), ErrorResponse.class).getErrors().get(0);
        assertAll(() -> assertEquals(500, exc.getRawStatusCode()),
                () -> assertEquals("INTERNAL_SERVER_ERROR", error.getErrorCode()),
                () -> assertEquals("Test exception", error.getMessage()));
    }

}
