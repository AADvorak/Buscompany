package net.thumbtack.school.buscompany.repository;

import net.thumbtack.school.buscompany.model.Trip;
import net.thumbtack.school.buscompany.model.TripDate;
import org.springframework.data.jpa.domain.Specification;

import javax.persistence.criteria.Root;
import javax.persistence.criteria.Subquery;
import java.time.LocalDate;

public class TripSpecifications {

    public static Specification<Trip> fromStationLike(String expression) {
        return (root, query, builder) -> builder.like(root.get("fromStation"), expression);
    }

    public static Specification<Trip> toStationLike(String expression) {
        return (root, query, builder) -> builder.like(root.get("toStation"), expression);
    }

    public static Specification<Trip> busNameLike(String expression) {
        return (root, query, builder) -> builder.like(root.get("bus").get("busName"), expression);
    }

    public static Specification<Trip> existsDateAfter(LocalDate fromDate) {
        return (root, query, builder) -> {
            Subquery<TripDate> subQuery = query.subquery(TripDate.class);
            Root<TripDate> tripDateRoot = subQuery.from(TripDate.class);
            return builder.exists(subQuery.select(tripDateRoot)
                    .where(builder.greaterThanOrEqualTo(tripDateRoot.get("date"), fromDate),
                            builder.equal(tripDateRoot.get("trip").get("id"), root.get("id"))));
        };
    }

    public static Specification<Trip> existsDateBefore(LocalDate toDate) {
        return (root, query, builder) -> {
            Subquery<TripDate> subQuery = query.subquery(TripDate.class);
            Root<TripDate> tripDateRoot = subQuery.from(TripDate.class);
            return builder.exists(subQuery.select(tripDateRoot)
                    .where(builder.lessThanOrEqualTo(tripDateRoot.get("date"), toDate),
                            builder.equal(tripDateRoot.get("trip").get("id"), root.get("id"))));
        };
    }

    public static Specification<Trip> isApproved() {
        return (root, query, builder) -> builder.equal(root.get("approved"), true);
    }

}
