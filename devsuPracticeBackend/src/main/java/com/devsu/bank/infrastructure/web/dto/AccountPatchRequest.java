package com.devsu.bank.infrastructure.web.dto;

import com.devsu.bank.domain.model.Account;
import com.devsu.bank.domain.model.AccountType;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;

public record AccountPatchRequest(
        @Pattern(regexp = ".*\\S.*", message = "{jakarta.validation.constraints.NotBlank.message}")
        @Size(max = 20) String accountNumber,
        AccountType accountType,
        @DecimalMin("0.00") @Digits(integer = 17, fraction = 2) BigDecimal initialBalance,
        Boolean status) {

    public Account toEntity() {
        return Account.builder()
                .accountNumber(accountNumber)
                .accountType(accountType)
                .initialBalance(initialBalance)
                .status(status)
                .build();
    }
}
