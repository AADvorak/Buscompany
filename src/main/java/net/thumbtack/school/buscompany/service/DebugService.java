package net.thumbtack.school.buscompany.service;

import net.thumbtack.school.buscompany.ApplicationProperties;
import net.thumbtack.school.buscompany.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class DebugService extends ServiceBase {

    private final UserRepository userRepository;

    private final TripRepository tripRepository;

    private final OrderRepository orderRepository;

    private final TripDateRepository tripDateRepository;

    public DebugService(UserSessionRepository userSessionRepository, ApplicationProperties applicationProperties,
                        UserRepository userRepository, TripRepository tripRepository, OrderRepository orderRepository,
                        TripDateRepository tripDateRepository) {
        super(userSessionRepository, applicationProperties);
        this.userRepository = userRepository;
        this.tripRepository = tripRepository;
        this.orderRepository = orderRepository;
        this.tripDateRepository = tripDateRepository;
    }

    public void clearAll() {
        if (!applicationProperties.isDebug()) {
            return;
        }
        tripRepository.deleteAll();
        userRepository.deleteAll();
    }

    @Transactional
    public void clearOrders() {
        if (!applicationProperties.isDebug()) {
            return;
        }
        orderRepository.deleteAll();
        tripDateRepository.resetAllFreePlaceCount();
    }

    public void testException() throws Exception {
        if (!applicationProperties.isDebug()) {
            return;
        }
        throw new Exception("Test exception");
    }

}
