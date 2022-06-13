package net.thumbtack.school.buscompany.service;

import lombok.RequiredArgsConstructor;
import net.thumbtack.school.buscompany.ApplicationProperties;
import net.thumbtack.school.buscompany.error.BusAppErrorCode;
import net.thumbtack.school.buscompany.error.BusAppException;
import net.thumbtack.school.buscompany.model.User;
import net.thumbtack.school.buscompany.model.UserSession;
import net.thumbtack.school.buscompany.repository.UserSessionRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class ServiceBase {

    protected final UserSessionRepository userSessionRepository;

    protected final ApplicationProperties applicationProperties;

    protected User getUserBySessionId(String sessionId) throws BusAppException {
        UserSession userSession = findActiveSession(sessionId);
        if (userSession == null) {
            throw new BusAppException(BusAppErrorCode.SESSION_NOT_FOUND);
        } else {
            userSession.setLastActionTime(LocalDateTime.now());
            userSessionRepository.save(userSession);
        }
        return userSession.getId().getUser();
    }

    protected UserSession findActiveSession(String sessionId) {
        return userSessionRepository.findActiveSession(sessionId, LocalDateTime.now(), applicationProperties.getUserIdleTimeout());
    }

}
