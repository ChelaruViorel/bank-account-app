package vio.account.requester.model;

import org.apache.commons.lang3.StringUtils;

import static org.apache.commons.lang3.StringUtils.isEmpty;

public enum AccountType {
    CURRENT(1), SAVINGS(2);

    private int id;

    private AccountType(int id) {
        this.id = id;
    }

    public static AccountType fromName(String name) {
        if (isEmpty(name)) {
            return null;
        }

        AccountType result = null;
        try {
            result = AccountType.valueOf(name.toUpperCase());
        } catch (Exception e) {

        }
        return result;
    }

    public int getId() {
        return id;
    }
}
