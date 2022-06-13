package net.thumbtack.school.buscompany.repository;

import net.thumbtack.school.buscompany.model.Bus;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BusRepository extends JpaRepository<Bus, Integer> {}
