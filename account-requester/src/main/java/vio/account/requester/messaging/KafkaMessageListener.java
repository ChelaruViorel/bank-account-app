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
import vio.account.requester.dao.AccountDao;
import vio.account.requester.model.AccountRequest;
import vio.account.requester.service.AccountService;

import static vio.account.requester.model.AccountRequestStatus.NEW;

@Slf4j
@Component
public class KafkaMessageListener {

    @Autowired
    private AccountService accountService;

    @KafkaListener(topics = "${spring.kafka.topic.account.request}", groupId = "${spring.kafka.consumer.group-id}")
    @SendTo
    public BaseMessage consumeMessageAccountRequest(@Payload MessageRequestAccount message) {
        log.info("processing kafka message: " + message);

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

        //response with a kafka message that the request was created
        MessageRequestAccountResponse messageResponse = new MessageRequestAccountResponse();
        messageResponse.setStatus("SUCCESS");
        messageResponse.setMessage("Request for creating a SAVINGS account was created successfully !");
        messageResponse.setAccountRequestId(requestId);
        messageResponse.setClientCnp(message.getClientCnp());
        return messageResponse;
    }
}

