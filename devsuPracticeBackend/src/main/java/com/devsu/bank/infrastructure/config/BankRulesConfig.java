package com.devsu.bank.infrastructure.config;

import com.devsu.bank.domain.model.DailyWithdrawalLimit;
import java.math.BigDecimal;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class BankRulesConfig {

    @Bean
    public DailyWithdrawalLimit dailyWithdrawalLimit(@Value("${bank.daily-withdrawal-limit}") BigDecimal amount) {
        return new DailyWithdrawalLimit(amount);
    }
}
