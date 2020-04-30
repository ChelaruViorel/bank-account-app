package vio.account.requester.model;


import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static vio.account.requester.model.AccountRequestStatus.PROCESSED;
import static vio.account.requester.model.AccountRequestStatus.fromId;

public class TestAccountRequestStatus {

    @Test
    public void testFromId(){
        Optional<AccountRequestStatus> statusOpt = fromId(3);
        assertFalse(statusOpt.isEmpty());
        assertEquals(PROCESSED, statusOpt.get());

        statusOpt = fromId(10000);
        assertTrue(statusOpt.isEmpty());

        statusOpt = fromId(null);
        assertTrue(statusOpt.isEmpty());
    }
}
