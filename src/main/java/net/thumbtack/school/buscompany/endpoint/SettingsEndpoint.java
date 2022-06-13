package net.thumbtack.school.buscompany.endpoint;

import lombok.RequiredArgsConstructor;
import net.thumbtack.school.buscompany.dto.response.SettingsDtoResponse;
import net.thumbtack.school.buscompany.error.BusAppException;
import net.thumbtack.school.buscompany.service.SettingsService;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/settings")
@RequiredArgsConstructor
public class SettingsEndpoint extends EndpointBase {

    private final SettingsService settingsService;

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public SettingsDtoResponse getSettings(@CookieValue(name = JAVASESSIONID, defaultValue = "") String sessionId) throws BusAppException {
        return settingsService.getSettings(sessionId);
    }

}
