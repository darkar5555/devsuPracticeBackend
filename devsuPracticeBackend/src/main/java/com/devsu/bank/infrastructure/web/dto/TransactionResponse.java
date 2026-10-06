package com.devsu.bank.infrastructure.web.dto;

import com.devsu.bank.domain.model.Transaction;
import com.devsu.bank.domain.model.TransactionType;
import java.math.BigDecimal;
import java.time.LocalDateTime;

public record TransactionResponse(
        Long id,
        LocalDateTime date,
        TransactionType transactionType,
        BigDecimal amount,
        BigDecimal balance,
        String accountNumber) {

    public static TransactionResponse from(Transaction transaction) {
        return new TransactionResponse(
                transaction.getId(),
                transaction.getDate(),
                transaction.getTransactionType(),
                transaction.getAmount(),
                transaction.getBalance(),
                transaction.getAccount().getAccountNumber());
    }
}
