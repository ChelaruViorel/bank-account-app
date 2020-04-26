package vio.account.requester.controller;

import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.requestreply.ReplyingKafkaTemplate;
import org.springframework.kafka.requestreply.RequestReplyFuture;
import org.springframework.web.bind.annotation.*;
import vio.account.requester.messaging.BaseMessage;
import vio.account.requester.messaging.MessageRequestAccount;
import vio.account.requester.messaging.MessageRequestAccountResponse;
import vio.account.requester.model.AccountType;
import vio.account.requester.service.AccountService;
import vio.account.requester.service.ClientService;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.concurrent.ExecutionException;

import static java.time.Duration.ofMinutes;
import static java.time.Instant.now;
import static org.apache.commons.lang3.StringUtils.isEmpty;
import static org.springframework.http.HttpStatus.*;
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
    public RequestAccountResponse requestAccount(@PathVariable String accountType, @RequestBody RequestAccountData requestAccountData, HttpServletResponse response) throws IOException {
        //FIXME throw exceptions that map to status codes

        AccountType accountTypeVar = fromName(accountType.toUpperCase());
        if (accountTypeVar == null) {
            response.sendError(NOT_FOUND.value(), "Account type "+accountType+" does not exist !");
            return null;
        }

        if (isEmpty(requestAccountData.getClientCnp())) {
            response.sendError(BAD_REQUEST.value(), "Client CNP must not be empty !");
            return null;
        }

        if (requestAccountData.getInitialDeposit() < 0) {
            response.sendError(BAD_REQUEST.value(), "Initial deposit cannot be a negative number !");
            return null;
        }

        boolean clientExists = clientService.clientExists(requestAccountData.getClientCnp());
        if (!clientExists) {
            log.debug("Client not found for cnp: " + requestAccountData.getClientCnp() + " !");
            response.sendError(BAD_REQUEST.value(), "Client with cnp "+requestAccountData.getClientCnp()+" was not found !");
            return null;
        }

        boolean accountAlreadyExists = accountService.hasClientAccount(requestAccountData.getClientCnp(), accountTypeVar);
        if (accountAlreadyExists) {
            log.debug("for client cnp: " + requestAccountData.getClientCnp() + " account already exists: " + accountAlreadyExists);
            response.sendError(CONFLICT.value(), "Cannot create savings account ! Client already has a savings account !");
            return null;
        }

        Long lastActiveRequest = accountService.getLastActiveAccountRequestId(requestAccountData.getClientCnp(), accountTypeVar);
        if (lastActiveRequest != null) {
            log.debug("for client cnp: " + requestAccountData.getClientCnp() + " last active account request id: " + lastActiveRequest);
            return new RequestAccountResponse("SUCCESS", "Request for the creation of SAVINGS account was successfully created !", lastActiveRequest);
        }

        MessageRequestAccount msg = MessageRequestAccount.builder()
                .initialDeposit(requestAccountData.getInitialDeposit())
                .accountType(SAVINGS)
                .build();
        msg.setClientCnp(requestAccountData.getClientCnp());
        msg.setAgentUsername("AGENT_GARCEA");
        msg.setRequestTimestamp(now());

        log.info("sending kafka request to topic: " + topicAccountRequests + " ..... ");
        ProducerRecord<String, BaseMessage> record = new ProducerRecord<>(topicAccountRequests, null, accountTypeVar.name(), msg);
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

    @GetMapping("/{accountType}/request/{requestId}/status")
    public RequestAccountStatusResponse getAccountRequestStatus(@PathVariable String accountType, @PathVariable Long requestId, HttpServletResponse response) throws IOException {
        //FIXME throw exceptions that map to status codes

        AccountType accountTypeVar = fromName(accountType.toUpperCase());
        if (accountTypeVar == null) {
            response.sendError(NOT_FOUND.value(), "Account type "+accountType+" does not exist !");
            return null;
        }

        String status = accountService.getAccountRequestStatus(requestId);

        if (status == null) {
            response.sendError(NOT_FOUND.value(), "Could not find account request with id=" + requestId + " !");
            return null;
        }

        return new RequestAccountStatusResponse(status);
    }
}
