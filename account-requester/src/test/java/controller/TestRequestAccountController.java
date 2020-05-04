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
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import static java.util.concurrent.CompletableFuture.completedFuture;
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
    public void getAccountRequestStatus_emptyAccountType() throws IOException {
        Exception e = assertThrows(AccountTypeNotExistsException.class, () -> {
            controller.getAccountRequestStatus("", 1L);
        });
        assertNotNull(e);
    }

    @Test
    public void getAccountRequestStatus_invalidAccountType() throws IOException {
        Exception e = assertThrows(AccountTypeNotExistsException.class, () -> {
            controller.getAccountRequestStatus("BLA BLA", 1L);
        });
        assertNotNull(e);
    }

    @Test
    public void getAccountRequestStatus_invalidRequest() throws IOException {
        when(accountService.getAccountRequestStatus(1L)).thenReturn(completedFuture(Optional.empty()));

        Exception e = assertThrows(ExecutionException.class, () -> {
            controller.getAccountRequestStatus("SAVINGS", 1L).get();
        });
        assertNotNull(e);
        assertTrue(e.getMessage().contains("Could not find account request with id"));
    }

    @Test
    public void getAccountRequestStatus_success() throws IOException, ExecutionException, InterruptedException {
        when(accountService.getAccountRequestStatus(1L)).thenReturn(completedFuture(Optional.of(PROCESSED)));
        RequestAccountStatusResponse response = controller.getAccountRequestStatus("SAVINGS", 1L).get();
        assertNotNull(response);
        assertFalse(StringUtils.isEmpty(response.getStatus()));
        assertEquals("PROCESSED", response.getStatus());

    }

}
