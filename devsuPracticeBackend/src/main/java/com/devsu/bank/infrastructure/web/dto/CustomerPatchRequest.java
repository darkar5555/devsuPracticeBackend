package com.devsu.bank.infrastructure.web.dto;

import com.devsu.bank.domain.model.Customer;
import com.devsu.bank.domain.model.Gender;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record CustomerPatchRequest(
        @Pattern(regexp = ".*\\S.*", message = "{jakarta.validation.constraints.NotBlank.message}")
        @Size(max = 100) String name,
        Gender gender,
        @Min(0) @Max(150) Integer age,
        @Pattern(regexp = ".*\\S.*", message = "{jakarta.validation.constraints.NotBlank.message}")
        @Size(max = 20) String identification,
        @Pattern(regexp = ".*\\S.*", message = "{jakarta.validation.constraints.NotBlank.message}")
        @Size(max = 200) String address,
        @Pattern(regexp = ".*\\S.*", message = "{jakarta.validation.constraints.NotBlank.message}")
        @Size(max = 20) String phone,
        @Size(min = 4, max = 100) String password,
        Boolean status) {

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
