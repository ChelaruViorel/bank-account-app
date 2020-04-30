package vio.account.requester.messaging;


import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import vio.account.requester.controller.RequestAccountResponse;
import vio.account.requester.dao.AccountDao;
import vio.account.requester.model.AccountRequest;
import vio.account.requester.service.AccountService;
import vio.account.requester.service.ClientService;

import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.CONFLICT;
import static vio.account.requester.model.AccountRequestStatus.NEW;

@Slf4j
@Component
public class KafkaMessageListener {

    @Autowired
    private AccountService accountService;

    @Autowired
    private ClientService clientService;

    @KafkaListener(topics = "${spring.kafka.topic.account.request}", groupId = "${spring.kafka.consumer.group-id}")
    @SendTo
    public BaseMessage consumeMessageAccountRequest(@Payload MessageRequestAccount message) {
        log.info("processing kafka message: " + message);
        MessageRequestAccountResponse messageResponse = new MessageRequestAccountResponse();

        boolean clientExists = clientService.clientExists(message.getClientCnp());
        log.debug("for client cnp: " + message.getClientCnp() + " client exists: " + clientExists);
        if (!clientExists) {
            messageResponse.setAccountRequestId(-1);
            messageResponse.setStatus("FAILED");
            messageResponse.setMessage("Client with cnp "+message.getClientCnp()+" was not found !");
            return messageResponse;
        }

        boolean accountAlreadyExists = accountService.hasClientAccount(message.getClientCnp(), message.getAccountType());
        log.debug("for client cnp: " + message.getClientCnp() + " account already exists: " + accountAlreadyExists);
        if (accountAlreadyExists) {
            messageResponse.setAccountRequestId(-1);
            messageResponse.setStatus("FAILED");
            messageResponse.setMessage("Cannot create savings account ! Client "+message.getClientCnp()+" already has a savings account !");
            return messageResponse;
        }

        Long lastActiveRequest = accountService.getLastActiveAccountRequestId(message.getClientCnp(), message.getAccountType());
        log.debug("for client cnp: " + message.getClientCnp() + " last active account request id: " + lastActiveRequest);
        if (lastActiveRequest != null) {
            messageResponse.setAccountRequestId(lastActiveRequest);
            messageResponse.setStatus("SUCCESS");
            messageResponse.setMessage("Request for the creation of SAVINGS account already exists ! A new one will not be created until the current one is processed !");
            return messageResponse;
        }

        //CREATE A NEW ACCOUNT REQUEST
        //build the request object
        AccountRequest request = new AccountRequest();
        request.setClientCnp(message.getClientCnp());
        request.setAccountType(message.getAccountType());
        request.setInitialDeposit(message.getInitialDeposit());
        request.setAgentUsername(message.getAgentUsername());
        request.setRequestTimestamp(message.getRequestTimestamp());
        request.setStatus(NEW);

        //insert request into the database
        long requestId = accountService.createAccountRequest(request);
        log.debug("for client cnp: " + message.getClientCnp() + " created new account request, id: " + requestId);

        //response with a kafka message that the request was created
        messageResponse.setStatus("SUCCESS");
        messageResponse.setMessage("Request for creating a SAVINGS account was created successfully !");
        messageResponse.setAccountRequestId(requestId);
        messageResponse.setClientCnp(message.getClientCnp());
        return messageResponse;
    }
}

