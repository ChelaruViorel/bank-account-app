package controller;

import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.requestreply.ReplyingKafkaTemplate;
import vio.account.requester.controller.RequestAccountController;
import vio.account.requester.controller.RequestAccountStatusResponse;
import vio.account.requester.controller.exceptions.AccountRequestNotFoundException;
import vio.account.requester.controller.exceptions.AccountTypeNotExistsException;
import vio.account.requester.messaging.BaseMessage;
import vio.account.requester.service.AccountService;
import vio.account.requester.service.ClientService;

import java.io.IOException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;
import static vio.account.requester.model.AccountRequestStatus.PROCESSED;

@ExtendWith(MockitoExtension.class)
public class TestRequestAccountController {

    @InjectMocks
    RequestAccountController controller;

    @Mock
    ReplyingKafkaTemplate<String, BaseMessage, BaseMessage> replyingKafkaTemplate;

    @Mock
    AccountService accountService;

    @Mock
    ClientService clientService;

    @Test
    public void getAccountRequestStatus_invalidAccountType() throws IOException {
        Exception e = assertThrows(AccountTypeNotExistsException.class, () -> {
            controller.getAccountRequestStatus("BLA BLA", 1L);
        });
        assertNotNull(e);
    }

    @Test
    public void getAccountRequestStatus_invalidRequest() throws IOException {
        when(accountService.getAccountRequestStatus(1L)).thenReturn(Optional.empty());

        Exception e = assertThrows(AccountRequestNotFoundException.class, () -> {
            controller.getAccountRequestStatus("SAVINGS", 1L);
        });
        assertNotNull(e);
    }

    @Test
    public void getAccountRequestStatus_success() throws IOException {
        when(accountService.getAccountRequestStatus(1L)).thenReturn(Optional.of(PROCESSED));
        RequestAccountStatusResponse response = controller.getAccountRequestStatus("SAVINGS", 1L);
        assertNotNull(response);
        assertFalse(StringUtils.isEmpty(response.getStatus()));
        assertEquals("PROCESSED", response.getStatus());

    }

}
