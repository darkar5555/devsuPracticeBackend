package com.devsu.bank.domain.exception;

public class InsufficientBalanceException extends BusinessException {

    public InsufficientBalanceException() {
        super("Saldo no disponible");
    }
}
