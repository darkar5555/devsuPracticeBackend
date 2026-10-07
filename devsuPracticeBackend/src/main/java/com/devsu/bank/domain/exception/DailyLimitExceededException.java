package com.devsu.bank.domain.exception;

public class DailyLimitExceededException extends BusinessException {

    public DailyLimitExceededException() {
        super("Cupo diario Excedido");
    }
}
