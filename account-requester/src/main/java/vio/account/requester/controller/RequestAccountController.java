package vio.account.requester.controller;

import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.requestreply.ReplyingKafkaTemplate;
import org.springframework.kafka.requestreply.RequestReplyFuture;
import org.springframework.validation.FieldError;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;
import vio.account.requester.controller.exceptions.AccountRequestNotFoundException;
import vio.account.requester.controller.exceptions.AccountTypeNotExistsException;
import vio.account.requester.controller.exceptions.InvalidWebFieldException;
import vio.account.requester.messaging.BaseMessage;
import vio.account.requester.messaging.MessageRequestAccount;
import vio.account.requester.messaging.MessageRequestAccountResponse;
import vio.account.requester.model.AccountType;
import vio.account.requester.service.AccountService;
import vio.account.requester.service.ClientService;

import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

import static java.time.Duration.ofMinutes;
import static java.time.Instant.now;
import static java.util.concurrent.CompletableFuture.completedFuture;
import static org.apache.commons.lang3.StringUtils.isEmpty;
import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static vio.account.requester.model.AccountType.SAVINGS;
import static vio.account.requester.model.AccountType.fromName;

@Slf4j
@RestController
@RequestMapping("/api/v1/account")
@Validated
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
    public CompletableFuture<RequestAccountResponse> requestAccount(@PathVariable @NotEmpty String accountType,
                                                                    @Valid @RequestBody RequestAccountData requestAccountData) {

        validateRequestAccountParams(accountType, requestAccountData);

        Optional<AccountType> accountTypeVar = fromName(accountType.toUpperCase());
        MessageRequestAccount msg = buildMessageRequestAccount(requestAccountData);

        log.info("sending kafka request to topic: " + topicAccountRequests + " ..... ");
        ProducerRecord<String, BaseMessage> record = new ProducerRecord<>(topicAccountRequests, null, accountTypeVar.get().name(), msg);
        RequestReplyFuture<String, BaseMessage, BaseMessage> sendMessageFuture = replyingKafkaTemplate.sendAndReceive(record, ofMinutes(3));

        return sendMessageFuture.completable().thenCompose(responseRecord -> {
            MessageRequestAccountResponse responseMessage = (MessageRequestAccountResponse) responseRecord.value();
            log.debug("received reply message: " + responseMessage);
            RequestAccountResponse response = new RequestAccountResponse(responseMessage.getStatus(), responseMessage.getMessage(), responseMessage.getAccountRequestId());
            return CompletableFuture.completedFuture(response);
        });
    }

    private void validateRequestAccountParams(String accountType, RequestAccountData requestAccountData) {
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

    private MessageRequestAccount buildMessageRequestAccount(RequestAccountData requestAccountData) {
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
    public CompletableFuture<RequestAccountStatusResponse> getAccountRequestStatus(@PathVariable @NotEmpty String accountType,
                                                                                   @PathVariable @NotNull Long requestId) {

        if (fromName(accountType.toUpperCase()).isEmpty()) {
            throw new AccountTypeNotExistsException(accountType);
        }

        return accountService.getAccountRequestStatus(requestId)
                .thenCompose(status -> {
                    if (status.isEmpty()) {
                        throw new AccountRequestNotFoundException(requestId);
                    }
                    return completedFuture(new RequestAccountStatusResponse(status.get().name()));
                });
    }

    @ResponseStatus(BAD_REQUEST)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public Map<String, String> handleValidationExceptions(
            MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach((error) -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });
        return errors;
    }
}
