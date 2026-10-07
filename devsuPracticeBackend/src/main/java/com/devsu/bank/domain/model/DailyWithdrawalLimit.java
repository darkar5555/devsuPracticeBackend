package com.devsu.bank.domain.model;

import java.math.BigDecimal;

// Max total amount of withdrawals per day
public record DailyWithdrawalLimit(BigDecimal amount) {

    public boolean isExceededBy(BigDecimal total) {
        return total.compareTo(amount) > 0;
    }
}
