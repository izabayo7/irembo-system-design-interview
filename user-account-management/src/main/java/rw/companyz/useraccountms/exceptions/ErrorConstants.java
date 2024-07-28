package rw.companyz.useraccountms.exceptions;

import java.net.URI;
public final class ErrorConstants {

    public static final String ERR_CONCURRENCY_FAILURE = "error.concurrencyFailure";
    public static final String ERR_VALIDATION = "error.validation";
    public static final String PROBLEM_BASE_URL = "https://www.companyz.com/problem";
    public static final URI DEFAULT_TYPE = URI.create(PROBLEM_BASE_URL + "/problem-with-message");
    public static final URI ENTITY_NOT_FOUND_TYPE = URI.create(PROBLEM_BASE_URL + "/entity-not-found");
    public static final URI INVALID_INPUT__EXCEPTION_KEY = URI.create(PROBLEM_BASE_URL + "/form-input-data-invalid");

    private ErrorConstants() {
    }
}

