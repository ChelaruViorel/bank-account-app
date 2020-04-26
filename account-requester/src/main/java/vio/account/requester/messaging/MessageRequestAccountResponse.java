package vio.account.requester.messaging;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class MessageRequestAccountResponse extends BaseMessage{
    private long accountRequestId;
}
