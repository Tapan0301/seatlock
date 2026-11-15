package com.seatlock.payment.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@RestControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(PaymentNotFoundException.class)
    ProblemDetail handleNotFound(PaymentNotFoundException ex){
        return problem(HttpStatus.NOT_FOUND, "PAYMENT_NOT_FOUND", ex.getMessage());
    }

    @ExceptionHandler(IdempotencyKeyReuseException.class)
    ProblemDetail handleKeyReuse(IdempotencyKeyReuseException ex){
        return problem(HttpStatus.valueOf(422), "IDEMPOTENCY_KEY_REUSED", ex.getMessage());
    }

    @ExceptionHandler(PaymentInProgressException.class)
    ProblemDetail handleInProgress(PaymentInProgressException ex){
        return problem(HttpStatus.CONFLICT, "PAYMENT_IN_PROGRESS", ex.getMessage());
    }

    private static  ProblemDetail problem(HttpStatus status, String code, String detail){
        ProblemDetail problem = ProblemDetail.forStatusAndDetail(status, detail);
        problem.setProperty("code", code);
        return  problem;
    }
}
