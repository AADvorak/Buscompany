package net.thumbtack.school.buscompany.repository;

import net.thumbtack.school.buscompany.model.TripDate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDate;

public interface TripDateRepository extends JpaRepository<TripDate, Integer> {

    @Modifying
    @Query(value = "delete from trip_date where trip_id = ?", nativeQuery = true)
    void deleteAllByTripId(int tripId);

    TripDate findByTripIdAndDate(int tripId, LocalDate date);

    @Modifying
    @Query(value = "update trip_date " +
            "set free_place_count = free_place_count - ?2 " +
            "where id = ?1 and free_place_count >= ?2",
            nativeQuery = true)
    int checkAndDecreaseFreePlaceCount(int tripDateId, int count);

    @Modifying
    @Query(value = "update trip_date " +
            "set free_place_count = free_place_count + ?2 " +
            "where id = ?1",
            nativeQuery = true)
    void increaseFreePlaceCount(int tripDateId, int count);

    @Modifying
    @Query(value = "update trip_date td " +
            "join trip t on t.id = td.trip_id " +
            "join bus b on b.id = t.bus_id " +
            "set td.free_place_count = b.place_count " +
            "where td.free_place_count != b.place_count",
            nativeQuery = true)
    void resetAllFreePlaceCount();

}
