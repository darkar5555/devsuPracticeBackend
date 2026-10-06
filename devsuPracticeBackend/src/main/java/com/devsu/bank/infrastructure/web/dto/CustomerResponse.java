package com.devsu.bank.infrastructure.web.dto;

import com.devsu.bank.domain.model.Customer;
import com.devsu.bank.domain.model.Gender;

public record CustomerResponse(
        Long id,
        String name,
        Gender gender,
        Integer age,
        String identification,
        String address,
        String phone,
        Boolean status) {

    public static CustomerResponse from(Customer customer) {
        return new CustomerResponse(
                customer.getId(),
                customer.getName(),
                customer.getGender(),
                customer.getAge(),
                customer.getIdentification(),
                customer.getAddress(),
                customer.getPhone(),
                customer.getStatus());
    }
}
