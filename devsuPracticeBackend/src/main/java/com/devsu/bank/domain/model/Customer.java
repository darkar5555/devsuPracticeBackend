package com.devsu.bank.domain.model;

import jakarta.persistence.AttributeOverride;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Entity
@Table(
        name = "customer",
        uniqueConstraints = @UniqueConstraint(
                name = "uk_customer_identification",
                columnNames = "identification"))
@AttributeOverride(name = "id", column = @Column(name = "customer_id"))
@Getter
@Setter
@NoArgsConstructor
@SuperBuilder
public class Customer extends Person {

    @Column(nullable = false, length = 100)
    private String password;

    @Column(nullable = false)
    private Boolean status;
}
