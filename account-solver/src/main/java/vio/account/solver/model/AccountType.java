package vio.account.solver.model;

import java.util.Arrays;
import java.util.Optional;

public enum AccountType {
    CURRENT(1), SAVINGS(2);

    private Integer id;

    private AccountType(int id) {
        this.id = id;
    }

    public static Optional<AccountType> fromId(Integer id) {
        return Arrays.stream(AccountType.values())
                .filter(accountType -> accountType.getId().equals(id))
                .findFirst();
    }

    public Integer getId() {
        return id;
    }
}
