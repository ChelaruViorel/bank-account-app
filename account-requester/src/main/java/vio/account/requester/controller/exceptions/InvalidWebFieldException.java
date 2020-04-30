package vio.account.requester.controller.exceptions;

import org.springframework.web.bind.annotation.ResponseStatus;

import static org.springframework.http.HttpStatus.BAD_REQUEST;

@ResponseStatus(BAD_REQUEST)
public class InvalidWebFieldException extends RuntimeException{

    public InvalidWebFieldException(String errorMessage) {
        super("Invalid web field: "+errorMessage);
    }
}
