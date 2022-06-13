package net.thumbtack.school.buscompany.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Getter
@Setter
@RequiredArgsConstructor
@AllArgsConstructor
public class Place {

    @Id
    @Column
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column
    private int number;

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "trip_date_id")
    private TripDate tripDate;

    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "passenger_id")
    private Passenger passenger;
}
