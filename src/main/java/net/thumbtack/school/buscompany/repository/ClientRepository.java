package net.thumbtack.school.buscompany.repository;

import net.thumbtack.school.buscompany.model.Client;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ClientRepository extends JpaRepository<Client, Integer> {}
