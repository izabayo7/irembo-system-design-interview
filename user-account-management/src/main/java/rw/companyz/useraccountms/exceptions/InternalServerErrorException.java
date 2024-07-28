package rw.companyz.useraccountms.exceptions;


import org.zalando.problem.AbstractThrowableProblem;
import org.zalando.problem.Status;

/**
 * Simple exception with a message, that returns an Internal Server Error code.
 */
public class InternalServerErrorException extends AbstractThrowableProblem {

    private static final long serialVersionUID = 1L;

    public InternalServerErrorException() {
        super(ErrorConstants.DEFAULT_TYPE, "An error occurred during request processing, contact system administrator to fix the issue", Status.INTERNAL_SERVER_ERROR);
    }
}

