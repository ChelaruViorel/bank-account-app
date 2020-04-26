package vio.account.requester.model;

import lombok.*;

import java.time.Instant;


@NoArgsConstructor
@Getter
@Setter
@ToString
public class AccountRequest {
    private Long id;
    private String clientCnp;
    private AccountType accountType;
    private double initialDeposit;
    private String agentUsername;
    private Instant requestTimestamp;
    private AccountRequestStatus status;
    private Instant processingStartTime;
    private Instant processingEndTime;
    private String workerName;
}
