package com.devsu.bank.infrastructure.persistence;

import com.devsu.bank.domain.model.Customer;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CustomerJpaRepository extends JpaRepository<Customer, Long> {

    Optional<Customer> findByIdentification(String identification);

    boolean existsByIdentification(String identification);
}
