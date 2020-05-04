package vio.account.requester.controller;

import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.PositiveOrZero;

@Slf4j
@NoArgsConstructor
@Data
public class RequestAccountData {
    @NotEmpty
    private String clientCnp;

    @PositiveOrZero
    private double initialDeposit;
}
