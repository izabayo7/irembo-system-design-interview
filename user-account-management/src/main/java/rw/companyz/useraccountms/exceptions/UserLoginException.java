package rw.companyz.useraccountms.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.io.Serial;

@ResponseStatus(value = HttpStatus.UNAUTHORIZED)
public class UserLoginException extends Exception {
    @Serial
    private static final long serialVersionUID = 1L;

    public UserLoginException(String message) {
        super(message);
    }
}

