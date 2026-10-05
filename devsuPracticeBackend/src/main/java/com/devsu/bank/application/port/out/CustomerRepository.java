package com.devsu.bank.application.port.out;

import com.devsu.bank.domain.model.Customer;
import java.util.List;
import java.util.Optional;

public interface CustomerRepository {

    Customer save(Customer customer);

    Optional<Customer> findById(Long id);

    Optional<Customer> findByIdentification(String identification);

    List<Customer> findAll();

    boolean existsByIdentification(String identification);

    void deleteById(Long id);
}
