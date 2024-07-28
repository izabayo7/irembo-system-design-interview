package rw.companyz.useraccountms.exceptions;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus (value = HttpStatus.BAD_REQUEST)
@Getter
@AllArgsConstructor
public class TokenRefreshException extends RuntimeException{

    private String message = "exceptions.tokenRefresh";
    private Object[] args;

    public TokenRefreshException(Object ...args){
        this.args = args;
    }
}
