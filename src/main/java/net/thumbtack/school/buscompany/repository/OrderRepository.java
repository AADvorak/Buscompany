package net.thumbtack.school.buscompany.repository;

import net.thumbtack.school.buscompany.model.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface OrderRepository extends JpaRepository<Order, Integer>, JpaSpecificationExecutor<Order> {

    Order findByIdAndClientId(int id, int clientId);

}
