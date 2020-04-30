package vio.account.requester.model;

import java.util.Optional;

import static org.apache.commons.lang3.StringUtils.isEmpty;

public enum AccountType {
    CURRENT(1), SAVINGS(2);

    private int id;

    private AccountType(int id) {
        this.id = id;
    }

    public static Optional<AccountType> fromName(String name) {
        AccountType result = null;
        try {
            result = AccountType.valueOf(name.toUpperCase());
        } catch (Exception e) {}

        return Optional.ofNullable(result);
    }

    public int getId() {
        return id;
    }
}
