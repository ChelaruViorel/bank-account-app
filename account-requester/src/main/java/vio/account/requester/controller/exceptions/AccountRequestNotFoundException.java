package vio.account.requester.controller.exceptions;

import org.springframework.web.bind.annotation.ResponseStatus;

import static org.springframework.http.HttpStatus.NOT_FOUND;

@ResponseStatus(NOT_FOUND)
public class AccountRequestNotFoundException extends RuntimeException {

    public AccountRequestNotFoundException(Long requestId) {
        super("Could not find account request with id=" + requestId + " !");
    }

}
