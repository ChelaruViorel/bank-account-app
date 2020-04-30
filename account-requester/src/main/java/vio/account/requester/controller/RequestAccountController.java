package vio.account.requester.controller;

import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.requestreply.ReplyingKafkaTemplate;
import org.springframework.kafka.requestreply.RequestReplyFuture;
import org.springframework.web.bind.annotation.*;
import vio.account.requester.controller.exceptions.AccountRequestNotFoundException;
import vio.account.requester.controller.exceptions.AccountTypeNotExistsException;
import vio.account.requester.controller.exceptions.InvalidWebFieldException;
import vio.account.requester.messaging.BaseMessage;
import vio.account.requester.messaging.MessageRequestAccount;
import vio.account.requester.messaging.MessageRequestAccountResponse;
import vio.account.requester.model.AccountRequestStatus;
import vio.account.requester.model.AccountType;
import vio.account.requester.service.AccountService;
import vio.account.requester.service.ClientService;

import java.io.IOException;
import java.util.Optional;
import java.util.concurrent.ExecutionException;

import static java.time.Duration.ofMinutes;
import static java.time.Instant.now;
import static org.apache.commons.lang3.StringUtils.isEmpty;
import static vio.account.requester.model.AccountType.SAVINGS;
import static vio.account.requester.model.AccountType.fromName;

@Slf4j
@RestController
@RequestMapping("/api/v1/account")
public class RequestAccountController {

    @Value("${spring.kafka.topic.account.request}")
    private String topicAccountRequests;

    @Autowired
    private ReplyingKafkaTemplate<String, BaseMessage, BaseMessage> replyingKafkaTemplate;

    @Autowired
    private AccountService accountService;

    @Autowired
    private ClientService clientService;

    @PostMapping(value = "/{accountType}/request", produces = "application/json", consumes = "application/json")
    public RequestAccountResponse requestAccount(@PathVariable String accountType, @RequestBody RequestAccountData requestAccountData) throws IOException {

        validateRequestAccountParams(accountType, requestAccountData);

        Optional<AccountType> accountTypeVar = fromName(accountType.toUpperCase());
        MessageRequestAccount msg = buildMessageRequestAccount(requestAccountData);

        log.info("sending kafka request to topic: " + topicAccountRequests + " ..... ");
        ProducerRecord<String, BaseMessage> record = new ProducerRecord<>(topicAccountRequests, null, accountTypeVar.get().name(), msg);
        RequestReplyFuture<String, BaseMessage, BaseMessage> future = replyingKafkaTemplate.sendAndReceive(record, ofMinutes(3));
        try {
            ConsumerRecord<String, BaseMessage> responseRecord = future.get();
            MessageRequestAccountResponse responseMessage = (MessageRequestAccountResponse) responseRecord.value();

            log.info("received reply message: " + responseMessage);
            return new RequestAccountResponse(responseMessage.getStatus(), responseMessage.getMessage(), responseMessage.getAccountRequestId());

        } catch (InterruptedException | ExecutionException e) {
            log.error(e.getMessage(), e);
            throw new RuntimeException(e);
        }
    }

    private void validateRequestAccountParams(String accountType, RequestAccountData requestAccountData){
        if (fromName(accountType.toUpperCase()).isEmpty()) {
            throw new AccountTypeNotExistsException(accountType);
        }

        if (isEmpty(requestAccountData.getClientCnp())) {
            throw new InvalidWebFieldException("Client CNP must not be empty !");
        }

        if (requestAccountData.getInitialDeposit() < 0) {
            throw new InvalidWebFieldException("Initial deposit cannot be a negative number !");
        }
    }

    private MessageRequestAccount buildMessageRequestAccount(RequestAccountData requestAccountData){
        MessageRequestAccount msg = MessageRequestAccount.builder()
                .initialDeposit(requestAccountData.getInitialDeposit())
                .accountType(SAVINGS)
                .build();
        msg.setClientCnp(requestAccountData.getClientCnp());
        msg.setAgentUsername("AGENT_GARCEA");
        msg.setRequestTimestamp(now());

        return msg;
    }

    @GetMapping("/{accountType}/request/{requestId}/status")
    public RequestAccountStatusResponse getAccountRequestStatus(@PathVariable String accountType, @PathVariable Long requestId) throws IOException {

        if (fromName(accountType.toUpperCase()).isEmpty()) {
            throw new AccountTypeNotExistsException(accountType);
        }

        Optional<AccountRequestStatus> status = accountService.getAccountRequestStatus(requestId);
        if (status.isEmpty()) {
            throw new AccountRequestNotFoundException(requestId);
        }

        return new RequestAccountStatusResponse(status.get().name());
    }
}
