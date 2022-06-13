package net.thumbtack.school.buscompany.endpoint;

import lombok.RequiredArgsConstructor;
import net.thumbtack.school.buscompany.dto.request.OrderDtoRequest;
import net.thumbtack.school.buscompany.dto.response.OrderDtoResponse;
import net.thumbtack.school.buscompany.error.BusAppException;
import net.thumbtack.school.buscompany.service.OrderService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class OrderEndpoint extends EndpointBase {

    private final OrderService orderService;

    @PostMapping(path = "/orders", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public OrderDtoResponse postOrder(@CookieValue(name = JAVASESSIONID, defaultValue = "") String sessionId,
                                      @Valid @RequestBody OrderDtoRequest request) throws BusAppException {
        return orderService.insertOrder(sessionId, request);
    }

    @GetMapping(path = "/orders", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<OrderDtoResponse> getOrders(@CookieValue(name = JAVASESSIONID, defaultValue = "") String sessionId,
                                            @RequestParam(required = false) String fromStation,
                                            @RequestParam(required = false) String toStation,
                                            @RequestParam(required = false) String busName,
                                            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fromDate,
                                            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate toDate,
                                            @RequestParam(required = false) Integer clientId) throws BusAppException {
        return orderService.findOrders(sessionId, decodeUTF8(fromStation), decodeUTF8(toStation), decodeUTF8(busName),
                fromDate, toDate, clientId);
    }

    @DeleteMapping("/orders/{id}")
    public void deleteOrder(@CookieValue(name = JAVASESSIONID, defaultValue = "") String sessionId,
                            @Valid @PathVariable int id) throws BusAppException {
        orderService.deleteOrder(sessionId, id);
    }

}
