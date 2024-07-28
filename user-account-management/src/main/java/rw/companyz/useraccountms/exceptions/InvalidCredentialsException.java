package rw.companyz.useraccountms.exceptions;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.UNAUTHORIZED)
@Getter
@AllArgsConstructor
public class InvalidCredentialsException extends RuntimeException{
    private String message = "exceptions.invalidCredentials";
    private Object[] args;

    public InvalidCredentialsException(){
        super("Message");
    }

    public InvalidCredentialsException(String errorMessage){
        super("Message");
        this.message = errorMessage;
    }
}
