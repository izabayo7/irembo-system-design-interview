package rw.companyz.useraccountms.exceptions;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.BAD_REQUEST)
@Getter
@AllArgsConstructor
public class BadRequestException extends Exception {
    private String message;
    private Object[] args;

    public BadRequestException(String message) {
        this.message = message;
        this.args = null;
    }
}
