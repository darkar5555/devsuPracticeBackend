package com.devsu.bank.infrastructure.web.dto;

import com.devsu.bank.domain.model.Customer;
import com.devsu.bank.domain.model.Gender;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record CustomerUpdateRequest(
        @NotBlank @Size(max = 100) String name,
        @NotNull Gender gender,
        @NotNull @Min(0) @Max(150) Integer age,
        @NotBlank @Size(max = 20) String identification,
        @NotBlank @Size(max = 200) String address,
        @NotBlank @Size(max = 20) String phone,
        @Size(min = 4, max = 100) String password,
        @NotNull Boolean status) {

    public Customer toEntity() {
        return Customer.builder()
                .name(name)
                .gender(gender)
                .age(age)
                .identification(identification)
                .address(address)
                .phone(phone)
                .password(password)
                .status(status)
                .build();
    }
}
