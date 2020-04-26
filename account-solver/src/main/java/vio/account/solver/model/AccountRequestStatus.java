package vio.account.solver.model;

public enum AccountRequestStatus {
    NEW(1), PROCESSING(2), PROCESSED(3), FAILED(4);

    private int id;
    private AccountRequestStatus(int id) {
        this.id = id;
    }

    public static AccountRequestStatus fromId(Integer id) {
        if(id == null) {
            return null;
        }
        for (AccountRequestStatus status : AccountRequestStatus.values()) {
            if (status.getId() == id.intValue()) {
                return status;
            }
        }

        return null;
    }

    public int getId() {
        return id;
    }
}
