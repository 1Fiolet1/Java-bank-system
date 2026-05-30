package ru.seleznev.domain;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AccountModel {

    private Long id;

    private BigDecimal balance = BigDecimal.ZERO;

    private UserModel owner;

    public AccountModel(BigDecimal balance, UserModel owner) {
        this.balance = balance;
        this.owner = owner;
    }

    public AccountModel(UserModel owner) {
        this.owner = owner;
        this.balance = BigDecimal.ZERO;
    }
}
