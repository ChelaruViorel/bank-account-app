package vio.account.requester.controller;

import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@NoArgsConstructor
@Data
public class RequestAccountData {
    private String clientCnp;
    private double initialDeposit;
}
