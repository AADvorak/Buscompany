package net.thumbtack.school.buscompany.repository;

import net.thumbtack.school.buscompany.model.UserSession;
import net.thumbtack.school.buscompany.model.UserSessionPK;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDateTime;

public interface UserSessionRepository extends JpaRepository<UserSession, UserSessionPK> {

    @Query(value = "select user_id, session_id, last_action_time\n" +
            "from user_session\n" +
            "where session_id = ? and timestampdiff(second, last_action_time, ?) < ?",
            nativeQuery = true)
    UserSession findActiveSession(String sessionId, LocalDateTime currentTime, int userIdleTimeout);

}
