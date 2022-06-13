package net.thumbtack.school.buscompany.repository;

import net.thumbtack.school.buscompany.model.Place;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface PlaceRepository extends JpaRepository<Place, Integer> {

    List<Place> findByTripDateIdAndPassengerIdNull(int tripDateId);

    List<Place> findByPassengerIdNotNull();

    Place findByTripDateIdAndPassengerId(int tripDateId, int passengerId);

    @Modifying
    @Query(value = "update place set passenger_id = ?1 " +
            "where trip_date_id = ?2 and number = ?3 and passenger_id is null",
            nativeQuery = true)
    int setPassengerToFreePlace(int passengerId, int tripDateId, int number);

}
