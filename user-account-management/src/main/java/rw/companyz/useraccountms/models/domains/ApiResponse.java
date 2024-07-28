package rw.companyz.useraccountms.models.domains;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import rw.companyz.useraccountms.utils.DateUtility;

@JsonIgnoreProperties(ignoreUnknown = true)
public class ApiResponse<T> {
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private T data;
    private String message = "";
    private HttpStatus status;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Object error = "";
    private final String timestamp = DateUtility.getTodayDateTimeInKigaliTimezone().toString();


    public ApiResponse() {
    }

    public ApiResponse(String message, Object error, HttpStatus status) {
        this.message = message;
        this.error = error;
        this.status = status;
    }
    public ApiResponse(T data, String message, Object error, HttpStatus status) {
        this.message = message;
        this.error = error;
        this.data = data;
        this.status = status;
    }

    public ApiResponse(T data) {
        this.data = data;
    }




    public ApiResponse(String message) {
        this.message = message;
    }

    public ApiResponse(T data, HttpStatus status) {
        this.data = data;
        this.status = status;
    }

    public ApiResponse(T data, String message) {
        this.data = data;
        this.message = message;
    }
    public ApiResponse(T data, String message, HttpStatus status) {
        this.data = data;
        this.message = message;
        this.status = status;
    }

    public ResponseEntity<ApiResponse<T>> toResponseEntity() {
        assert this.status != null;
        return ResponseEntity.status(this.status).body(this);
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public HttpStatus getStatus() {
        return status;
    }

    public void setStatus(HttpStatus status) {
        this.status = status;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setError(Object error) {
        this.error = error;
    }

    public Object getError() {
        return error;
    }
}


