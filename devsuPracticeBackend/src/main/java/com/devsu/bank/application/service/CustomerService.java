package com.devsu.bank.application.service;

import com.devsu.bank.application.port.out.CustomerRepository;
import com.devsu.bank.application.port.out.PasswordHasher;
import com.devsu.bank.domain.exception.DuplicateResourceException;
import com.devsu.bank.domain.exception.ResourceNotFoundException;
import com.devsu.bank.domain.model.Customer;
import java.util.List;
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
        Customer customer = getCustomer(id);
        if (!customer.getIdentification().equals(changes.getIdentification())) {
            ensureIdentificationIsFree(changes.getIdentification());
        }
        customer.setName(changes.getName());
        customer.setGender(changes.getGender());
        customer.setAge(changes.getAge());
        customer.setIdentification(changes.getIdentification());
        customer.setAddress(changes.getAddress());
        customer.setPhone(changes.getPhone());
        customer.setStatus(changes.getStatus());
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
                .orElseThrow(() -> new ResourceNotFoundException("Customer " + id + " not found"));
    }

    private void ensureIdentificationIsFree(String identification) {
        if (customerRepository.existsByIdentification(identification)) {
            throw new DuplicateResourceException(
                    "Customer with identification " + identification + " already exists");
        }
    }
}
