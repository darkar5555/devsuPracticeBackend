package com.devsu.bank.infrastructure.persistence;

import com.devsu.bank.application.port.out.CustomerRepository;
import com.devsu.bank.domain.model.Customer;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class CustomerRepositoryAdapter implements CustomerRepository {

    private final CustomerJpaRepository jpaRepository;

    @Override
    public Customer save(Customer customer) {
        return jpaRepository.save(customer);
    }

    @Override
    public Optional<Customer> findById(Long id) {
        return jpaRepository.findById(id);
    }

    @Override
    public Optional<Customer> findByIdentification(String identification) {
        return jpaRepository.findByIdentification(identification);
    }

    @Override
    public List<Customer> findAll() {
        return jpaRepository.findAll();
    }

    @Override
    public boolean existsByIdentification(String identification) {
        return jpaRepository.existsByIdentification(identification);
    }

    @Override
    public void deleteById(Long id) {
        jpaRepository.deleteById(id);
    }
}
