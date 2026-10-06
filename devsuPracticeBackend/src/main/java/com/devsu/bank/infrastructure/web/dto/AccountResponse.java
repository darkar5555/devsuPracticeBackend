package com.devsu.bank.infrastructure.web.dto;

import com.devsu.bank.domain.model.Account;
import com.devsu.bank.domain.model.AccountType;
import java.math.BigDecimal;

public record AccountResponse(
        Long id,
        String accountNumber,
        AccountType accountType,
        BigDecimal initialBalance,
        Boolean status,
        Long customerId,
        String customerName) {

    public static AccountResponse from(Account account) {
        return new AccountResponse(
                account.getId(),
                account.getAccountNumber(),
                account.getAccountType(),
                account.getInitialBalance(),
                account.getStatus(),
                account.getCustomer().getId(),
                account.getCustomer().getName());
    }
}
