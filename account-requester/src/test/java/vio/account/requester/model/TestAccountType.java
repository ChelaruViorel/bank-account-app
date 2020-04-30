package vio.account.requester.model;


import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static vio.account.requester.model.AccountType.SAVINGS;
import static vio.account.requester.model.AccountType.fromName;

public class TestAccountType {

    @Test
    public void testFromName(){
        Optional<AccountType> accountTypeOpt = fromName("SAVINGS");
        assertFalse(accountTypeOpt.isEmpty());
        assertEquals(SAVINGS, accountTypeOpt.get());

        accountTypeOpt = fromName("BLA BLA BLA");
        assertTrue(accountTypeOpt.isEmpty());

        accountTypeOpt = fromName(null);
        assertTrue(accountTypeOpt.isEmpty());
    }
}
