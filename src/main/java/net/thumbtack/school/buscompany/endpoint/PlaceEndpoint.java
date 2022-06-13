package net.thumbtack.school.buscompany.endpoint;

import lombok.RequiredArgsConstructor;
import net.thumbtack.school.buscompany.dto.request.PlaceDtoRequest;
import net.thumbtack.school.buscompany.dto.response.PlaceDtoResponse;
import net.thumbtack.school.buscompany.error.BusAppException;
import net.thumbtack.school.buscompany.service.PlaceService;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class PlaceEndpoint extends EndpointBase {

    private final PlaceService placeService;

    @GetMapping(path = "/places/{orderId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<Integer> getFreePlaces(@CookieValue(name = JAVASESSIONID, defaultValue = "") String sessionId,
                                       @Valid @PathVariable int orderId) throws BusAppException {
        return placeService.getFreePlaces(sessionId, orderId);
    }

    @PostMapping(path = "/places", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    public PlaceDtoResponse postPlace(@CookieValue(name = JAVASESSIONID, defaultValue = "") String sessionId,
                                      @Valid @RequestBody PlaceDtoRequest request) throws BusAppException {
        return placeService.choosePlace(sessionId, request);
    }

}
