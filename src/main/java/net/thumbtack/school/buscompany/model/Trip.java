package net.thumbtack.school.buscompany.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.hibernate.Hibernate;

import javax.persistence.*;
import java.math.BigDecimal;
import java.sql.Time;
import java.util.List;
import java.util.Objects;

@Entity
@Getter
@Setter
@RequiredArgsConstructor
@AllArgsConstructor
public class Trip {

    @Id
    @Column
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "bus_id", nullable = false)
    private Bus bus;

    @Column
    private String fromStation;

    @Column
    private String toStation;

    @Column
    private Time start;

    @Column
    private BigDecimal price;

    @Column
    private int duration;

    @OneToOne(fetch = FetchType.EAGER, mappedBy = "trip", cascade = CascadeType.ALL)
    @PrimaryKeyJoinColumn
    private TripSchedule schedule;

    @OneToMany(fetch = FetchType.EAGER, mappedBy = "trip", cascade = CascadeType.ALL)
    private List<TripDate> dates;

    @Column
    private boolean approved;

    public void setSchedule(TripSchedule schedule) {
        this.schedule = schedule;
        if (this.schedule != null) {
            this.schedule.setTrip(this);
        }
    }

    public void setDates(List<TripDate> dates) {
        if (this.dates == null) {
            this.dates = dates;
        } else {
            this.dates.clear();
            this.dates.addAll(dates);
        }
        this.dates.forEach(tripDate -> {
            tripDate.setTrip(this);
            tripDate.setFreePlaceCount(this.bus.getPlaceCount());
        });
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        Trip trip = (Trip) o;
        return Objects.equals(id, trip.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
