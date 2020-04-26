package vio.account.solver.model;

public enum AccountType {
    CURRENT(1), SAVINGS(2);

    private int id;

    private AccountType(int id) {
        this.id = id;
    }

    public static AccountType fromId(Integer id) {
        if(id == null) {
            return null;
        }
        for (AccountType type : AccountType.values()) {
            if (type.getId() == id.intValue()) {
                return type;
            }
        }

        return null;
    }

    public int getId() {
        return id;
    }
}
