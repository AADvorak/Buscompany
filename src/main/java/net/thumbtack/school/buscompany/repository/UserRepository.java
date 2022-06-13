package net.thumbtack.school.buscompany.repository;

import net.thumbtack.school.buscompany.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

public interface UserRepository extends JpaRepository<User, Integer> {

    User findByLoginIgnoreCaseAndPassword(String login, String password);

    @Modifying
    @Query(value = "delete from `user` where id = (select user_id from user_session where session_id = ?1)",
            nativeQuery = true)
    int deleteBySessionId(String sessionId);

}
