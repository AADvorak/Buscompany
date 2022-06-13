package net.thumbtack.school.buscompany.endpoint;

import lombok.RequiredArgsConstructor;
import net.thumbtack.school.buscompany.dto.request.*;
import net.thumbtack.school.buscompany.dto.response.AdminDtoResponse;
import net.thumbtack.school.buscompany.dto.response.ClientDtoResponse;
import net.thumbtack.school.buscompany.dto.response.ResponseWithSessionId;
import net.thumbtack.school.buscompany.dto.response.UserDtoResponse;
import net.thumbtack.school.buscompany.error.BusAppException;
import net.thumbtack.school.buscompany.service.UserService;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class UserEndpoint extends EndpointBase {

    private final UserService userService;

    @PostMapping(path = "/admins", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public AdminDtoResponse postAdmin(@Valid @RequestBody AdminDtoRequest admin, HttpServletResponse response) throws BusAppException {
        ResponseWithSessionId<AdminDtoResponse> responseWithSessionId = userService.insertAdmin(admin);
        response.addCookie(new Cookie(JAVASESSIONID, responseWithSessionId.getSessionId()));
        return responseWithSessionId.getResponse();
    }

    @PostMapping(path = "/clients", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ClientDtoResponse postClient(@Valid @RequestBody ClientDtoRequest client, HttpServletResponse response) throws BusAppException {
        ResponseWithSessionId<ClientDtoResponse> responseWithSessionId = userService.insertClient(client);
        response.addCookie(new Cookie(JAVASESSIONID, responseWithSessionId.getSessionId()));
        return responseWithSessionId.getResponse();
    }

    @PutMapping(path = "/admins", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public AdminDtoResponse putAdmin(@CookieValue(name = JAVASESSIONID, defaultValue = "") String sessionId,
                                     @Valid @RequestBody AdminEditDtoRequest request) throws BusAppException {
        return userService.editAdmin(sessionId, request);
    }

    @PutMapping(path = "/clients", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ClientDtoResponse putClient(@CookieValue(name = JAVASESSIONID, defaultValue = "") String sessionId,
                                       @Valid @RequestBody ClientEditDtoRequest request) throws BusAppException {
        return userService.editClient(sessionId, request);
    }

    @GetMapping("/clients")
    public List<ClientDtoResponse> getClients(@CookieValue(name = JAVASESSIONID, defaultValue = "") String sessionId) throws BusAppException {
        return userService.getAllClients(sessionId);
    }

    @PostMapping(path = "/sessions", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public UserDtoResponse postSession(@Valid @RequestBody LoginDtoRequest login, HttpServletResponse response) throws BusAppException {
        ResponseWithSessionId<UserDtoResponse> responseWithSessionId = userService.login(login);
        response.addCookie(new Cookie(JAVASESSIONID, responseWithSessionId.getSessionId()));
        return responseWithSessionId.getResponse();
    }

    @DeleteMapping("/sessions")
    public void deleteSession(@CookieValue(name = JAVASESSIONID, defaultValue = "") String sessionId) throws BusAppException {
        userService.logout(sessionId);
    }

    @DeleteMapping("/accounts")
    public void deleteAccount(@CookieValue(name = JAVASESSIONID, defaultValue = "") String sessionId) throws BusAppException {
        userService.deleteUser(sessionId);
    }

    @GetMapping("/accounts")
    public UserDtoResponse getAccount(@CookieValue(name = JAVASESSIONID, defaultValue = "") String sessionId) throws BusAppException {
        return userService.getUserInfo(sessionId);
    }

}
