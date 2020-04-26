package vio.account.solver.model;

import lombok.Data;

@Data
public class Account {
    private String iban;
    private AccountType accountType;
    private String clientCnp;
    private double currentAmount;
}
