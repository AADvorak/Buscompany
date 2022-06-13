package net.thumbtack.school.buscompany.repository;

import net.thumbtack.school.buscompany.model.Trip;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface TripRepository extends JpaRepository<Trip, Integer>, JpaSpecificationExecutor<Trip> {}
