package vio.account.requester.controller.exceptions;

import org.springframework.web.bind.annotation.ResponseStatus;

import static org.springframework.http.HttpStatus.NOT_FOUND;

@ResponseStatus(NOT_FOUND)
public class AccountTypeNotExistsException extends RuntimeException {

    public AccountTypeNotExistsException(String accountType) {
        super("Account type "+accountType+" does not exist !");
    }

}
