package vio.account.requester.messaging;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BaseMessage {
    protected String clientCnp;
    protected Instant requestTimestamp;
    protected String agentUsername;
    private String status;
    private String message;
}
