package vio.account.requester.model;

import java.util.Arrays;
import java.util.Optional;

public enum AccountRequestStatus {
    NEW(1), PROCESSING(2), PROCESSED(3), FAILED(4);

    private Integer id;

    private AccountRequestStatus(int id) {
        this.id = id;
    }

    public static Optional<AccountRequestStatus> fromId(Integer id) {
        return Arrays.stream(AccountRequestStatus.values()).filter(status -> status.getId().equals(id)).findAny();
    }

    public Integer getId() {
        return id;
    }

}
