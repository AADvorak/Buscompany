package net.thumbtack.school.buscompany.endpoint;

import lombok.RequiredArgsConstructor;
import net.thumbtack.school.buscompany.service.DebugService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/debug")
@RequiredArgsConstructor
public class DebugEndpoint {

    private final DebugService debugService;

    @PostMapping("/clear")
    public void clear() {
        debugService.clearAll();
    }

    @PostMapping("/clear/orders")
    public void clearOrders() {
        debugService.clearOrders();
    }

    @GetMapping("/exception")
    public void testException() throws Exception {
        debugService.testException();
    }

}
