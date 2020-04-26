package vio.account.requester.messaging;

import lombok.*;
import vio.account.requester.model.AccountType;

import java.time.LocalDateTime;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class MessageRequestAccount extends BaseMessage{
    private double initialDeposit;
    private AccountType accountType;
}
