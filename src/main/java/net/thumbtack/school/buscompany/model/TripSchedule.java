package net.thumbtack.school.buscompany.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.Objects;

@Entity
@Getter
@Setter
@RequiredArgsConstructor
@AllArgsConstructor
public class TripSchedule {

    @Id
    @Column(name = "trip_id")
    private int id;

    @OneToOne
    @MapsId
    @JoinColumn(name = "trip_id")
    private Trip trip;

    @Column
    private LocalDate fromDate;

    @Column
    private LocalDate toDate;

    @Column
    private String period;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TripSchedule that = (TripSchedule) o;
        return id == that.id && Objects.equals(trip, that.trip) && Objects.equals(fromDate, that.fromDate) && Objects.equals(toDate, that.toDate) && Objects.equals(period, that.period);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, trip, fromDate, toDate, period);
    }
}
