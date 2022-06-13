package net.thumbtack.school.buscompany.repository;

import net.thumbtack.school.buscompany.model.Passenger;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PassengerRepository extends JpaRepository<Passenger, Integer> {
}
