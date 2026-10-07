package com.devsu.bank.application.port.out;

public interface PasswordHasher {

    String hash(String rawPassword);

}
