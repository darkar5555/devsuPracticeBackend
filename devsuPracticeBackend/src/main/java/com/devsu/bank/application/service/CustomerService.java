package com.devsu.bank.application.service;

import com.devsu.bank.application.port.out.CustomerRepository;
import com.devsu.bank.application.port.out.PasswordHasher;
import com.devsu.bank.domain.exception.DuplicateResourceException;
import com.devsu.bank.domain.exception.ResourceNotFoundException;
import com.devsu.bank.domain.model.Customer;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class CustomerService {

    private final CustomerRepository customerRepository;
    private final PasswordHasher passwordHasher;

    public Customer create(Customer customer) {
        ensureIdentificationIsFree(customer.getIdentification());
        customer.setPassword(passwordHasher.hash(customer.getPassword()));
        return customerRepository.save(customer);
    }

    @Transactional(readOnly = true)
    public List<Customer> findAll() {
        return customerRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Customer findById(Long id) {
        return getCustomer(id);
    }

    public Customer update(Long id, Customer changes) {
        return patch(id, changes);
    }

    public Customer patch(Long id, Customer changes) {
        Customer customer = getCustomer(id);
        if (changes.getIdentification() != null
                && !changes.getIdentification().equals(customer.getIdentification())) {
            ensureIdentificationIsFree(changes.getIdentification());
        }
        Optional.ofNullable(changes.getName()).ifPresent(customer::setName);
        Optional.ofNullable(changes.getGender()).ifPresent(customer::setGender);
        Optional.ofNullable(changes.getAge()).ifPresent(customer::setAge);
        Optional.ofNullable(changes.getIdentification()).ifPresent(customer::setIdentification);
        Optional.ofNullable(changes.getAddress()).ifPresent(customer::setAddress);
        Optional.ofNullable(changes.getPhone()).ifPresent(customer::setPhone);
        Optional.ofNullable(changes.getStatus()).ifPresent(customer::setStatus);
        if (changes.getPassword() != null && !changes.getPassword().isBlank()) {
            customer.setPassword(passwordHasher.hash(changes.getPassword()));
        }
        return customerRepository.save(customer);
    }

    public void delete(Long id) {
        getCustomer(id);
        customerRepository.deleteById(id);
    }

    private Customer getCustomer(Long id) {
        return customerRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Cliente " + id + " no encontrado"));
    }

    private void ensureIdentificationIsFree(String identification) {
        if (customerRepository.existsByIdentification(identification)) {
            throw new DuplicateResourceException(
                    "Ya existe un cliente con la identificación " + identification);
        }
    }
}
