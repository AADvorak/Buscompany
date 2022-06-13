package net.thumbtack.school.buscompany.endpoint;

import lombok.RequiredArgsConstructor;
import net.thumbtack.school.buscompany.dto.request.TripDtoRequest;
import net.thumbtack.school.buscompany.dto.response.BusDtoResponse;
import net.thumbtack.school.buscompany.dto.response.TripDtoResponse;
import net.thumbtack.school.buscompany.error.BusAppException;
import net.thumbtack.school.buscompany.service.TripService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class TripEndpoint extends EndpointBase {

    private final TripService tripService;

    @GetMapping(path = "/buses", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<BusDtoResponse> getBuses(@CookieValue(name = JAVASESSIONID, defaultValue = "") String sessionId) throws BusAppException {
        return tripService.getAllBuses(sessionId);
    }

    @PostMapping(path = "/trips", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public TripDtoResponse postTrip(@CookieValue(name = JAVASESSIONID, defaultValue = "") String sessionId,
                                    @Valid @RequestBody TripDtoRequest request) throws BusAppException {
        return tripService.insertTrip(sessionId, request);
    }

    @PutMapping(path = "/trips/{id}", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public TripDtoResponse putTrip(@CookieValue(name = JAVASESSIONID, defaultValue = "") String sessionId,
                                   @Valid @RequestBody TripDtoRequest request, @Valid @PathVariable int id) throws BusAppException {
        return tripService.updateTrip(sessionId, request, id);
    }

    @PutMapping(path = "/trips/{id}/approve", produces = MediaType.APPLICATION_JSON_VALUE)
    public TripDtoResponse putTripApprove(@CookieValue(name = JAVASESSIONID, defaultValue = "") String sessionId,
                                          @Valid @PathVariable int id) throws BusAppException {
        return tripService.approveTripAndCreatePlaces(sessionId, id);
    }

    @GetMapping(path = "/trips/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public TripDtoResponse getTrip(@CookieValue(name = JAVASESSIONID, defaultValue = "") String sessionId,
                                   @Valid @PathVariable int id) throws BusAppException {
        return tripService.getTripInfo(sessionId, id);
    }

    @DeleteMapping("/trips/{id}")
    public void deleteTrip(@CookieValue(name = JAVASESSIONID, defaultValue = "") String sessionId,
                           @Valid @PathVariable int id) throws BusAppException {
        tripService.deleteTrip(sessionId, id);
    }

    @GetMapping(path = "/trips", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<TripDtoResponse> getTrips(@CookieValue(name = JAVASESSIONID, defaultValue = "") String sessionId,
                                          @RequestParam(required = false) String fromStation,
                                          @RequestParam(required = false) String toStation,
                                          @RequestParam(required = false) String busName,
                                          @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fromDate,
                                          @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate toDate) throws BusAppException {
        return tripService.findTrips(sessionId, decodeUTF8(fromStation), decodeUTF8(toStation), decodeUTF8(busName),
                fromDate, toDate);
    }

}
