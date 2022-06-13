package net.thumbtack.school.buscompany;

import com.fasterxml.jackson.core.JsonProcessingException;
import net.thumbtack.school.buscompany.dto.response.SettingsAdminDtoResponse;
import net.thumbtack.school.buscompany.dto.response.SettingsClientDtoResponse;
import net.thumbtack.school.buscompany.dto.response.SettingsDtoResponse;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class TestIntegrationSettings extends TestIntegrationBase {

    @BeforeAll
    public void beforeAll() {
        clearAll();
        createUsersInDatabase();
    }

    @Test
    public void testGetSettingsWithoutLogin() throws JsonProcessingException {
        ResponseEntity<String> response = template.getForEntity(fullUrl(SETTINGS_URL), String.class);
        SettingsDtoResponse settingsDtoResponse = mapper.readValue(response.getBody(), SettingsDtoResponse.class);
        assertAll(() -> assertEquals(response.getStatusCodeValue(), 200),
                () -> assertTrue(settingsDtoResponse.getMaxNameLength() > 0),
                () -> assertTrue(settingsDtoResponse.getMinPasswordLength() > 0));
    }

    @Test
    public void testGetSettingsAdmin() throws JsonProcessingException {
        ResponseEntity<String> response = template.exchange(fullUrl(SETTINGS_URL), HttpMethod.GET,
                new HttpEntity<>(loginAdmin()), String.class);
        SettingsAdminDtoResponse settingsDtoResponse = mapper.readValue(response.getBody(), SettingsAdminDtoResponse.class);
        assertAll(() -> assertEquals(response.getStatusCodeValue(), 200),
                () -> assertTrue(settingsDtoResponse.getMaxNameLength() > 0),
                () -> assertTrue(settingsDtoResponse.getMinPasswordLength() > 0),
                () -> assertTrue(settingsDtoResponse.getUserIdleTimeout() > 0));
    }

    @Test
    public void testGetSettingsClient() throws JsonProcessingException {
        ResponseEntity<String> response = template.exchange(fullUrl(SETTINGS_URL), HttpMethod.GET,
                new HttpEntity<>(loginClient()), String.class);
        SettingsClientDtoResponse settingsDtoResponse = mapper.readValue(response.getBody(), SettingsClientDtoResponse.class);
        assertAll(() -> assertEquals(response.getStatusCodeValue(), 200),
                () -> assertTrue(settingsDtoResponse.getMaxNameLength() > 0),
                () -> assertTrue(settingsDtoResponse.getMinPasswordLength() > 0));
    }

}
