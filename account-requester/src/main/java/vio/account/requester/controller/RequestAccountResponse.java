package vio.account.requester.controller;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@NoArgsConstructor
@AllArgsConstructor
@Data
public class RequestAccountResponse {
    private String status;
    private String message;
    private Long accountRequestId;
}
