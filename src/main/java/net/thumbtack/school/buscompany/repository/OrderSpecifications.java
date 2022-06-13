package net.thumbtack.school.buscompany.repository;

import net.thumbtack.school.buscompany.model.Client;
import net.thumbtack.school.buscompany.model.Order;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDate;

public class OrderSpecifications {

    public static Specification<Order> fromStationLike(String expression) {
        return (root, query, builder) -> builder.like(root.get("tripDate").get("trip").get("fromStation"), expression);
    }

    public static Specification<Order> toStationLike(String expression) {
        return (root, query, builder) -> builder.like(root.get("tripDate").get("trip").get("toStation"), expression);
    }

    public static Specification<Order> busNameLike(String expression) {
        return (root, query, builder) -> builder.like(root.get("tripDate").get("trip").get("bus").get("busName"), expression);
    }

    public static Specification<Order> dateIsAfter(LocalDate fromDate) {
        return (root, query, builder) -> builder.greaterThanOrEqualTo(root.get("tripDate").get("date"), fromDate);
    }

    public static Specification<Order> dateIsBefore(LocalDate toDate) {
        return (root, query, builder) -> builder.lessThanOrEqualTo(root.get("tripDate").get("date"), toDate);
    }

    public static Specification<Order> clientIs(Client client) {
        return (root, query, builder) -> builder.equal(root.get("client"), client);
    }

}
