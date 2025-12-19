package com.order.service.order_service.advice;

import com.order.service.order_service.model.exception.DataExistException;
import com.order.service.order_service.model.exception.NotFoundException;
import com.order.service.order_service.model.exception.PaymentFailedException;
import com.order.service.order_service.model.response.ErrorResponse;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.reactive.function.client.WebClientRequestException;

import java.time.LocalDateTime;
import java.util.Optional;

@Slf4j
@RestControllerAdvice
public class CommonControllerAdvice {

    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<ErrorResponse> handleNotFoundException(NotFoundException e, HttpServletRequest request) {
        log.error(e.getMessage());

        Throwable ex = unwrap(e);
        NotFoundException nfe = (ex instanceof NotFoundException) ? (NotFoundException) ex
                : new NotFoundException(ex.getMessage());
        return buildErrorResponse(nfe, HttpStatus.NOT_FOUND, request);
    }

    @ExceptionHandler(WebClientRequestException.class)
    public ResponseEntity<ErrorResponse> handleWebClientRequestExceptionException(WebClientRequestException e, HttpServletRequest request) {
        log.error(e.getMessage());

        return buildErrorResponse(e, HttpStatus.SERVICE_UNAVAILABLE, request);
    }

    @ExceptionHandler(EmptyResultDataAccessException.class)
    public ResponseEntity<ErrorResponse> handleEmptyResultDataAccessExceptionException(EmptyResultDataAccessException e, HttpServletRequest request) {
        log.error(e.getMessage());

        return buildErrorResponse(e, HttpStatus.NOT_FOUND, request);
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ErrorResponse> handleDataIntegrityViolationExceptionException(DataIntegrityViolationException e, HttpServletRequest request) {
        log.error(e.getMessage());

        return buildErrorResponse(e, HttpStatus.CONFLICT, request);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ErrorResponse> handleHttpMessageNotReadableExceptionException(HttpMessageNotReadableException e, HttpServletRequest request) {
        log.error(e.getMessage());

        return buildErrorResponse(e, HttpStatus.BAD_REQUEST, request);
    }

    @ExceptionHandler(DataExistException.class)
    public ResponseEntity<ErrorResponse> handleDataExistException(DataExistException e, HttpServletRequest request) {
        log.error(e.getMessage());

        Throwable ex = unwrap(e);
        DataExistException dee = (ex instanceof DataExistException) ? (DataExistException) ex
                : new DataExistException(ex.getMessage());
        return buildErrorResponse(dee, HttpStatus.CONFLICT, request);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResponse> handleIllegalArgumentExceptionException(IllegalArgumentException e, HttpServletRequest request) {
        log.error(e.getMessage());

        return buildErrorResponse(e, HttpStatus.CONFLICT, request);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleMethodArgumentNotValidExceptionException(MethodArgumentNotValidException e, HttpServletRequest request) {
        log.error(e.getMessage());

        return buildErrorResponse(e, HttpStatus.BAD_REQUEST, request);
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ErrorResponse> handleAccessDeniedException(AccessDeniedException e, HttpServletRequest request) {
        log.error(e.getMessage());

        return buildErrorResponse(e, HttpStatus.FORBIDDEN, request);
    }

    @ExceptionHandler(PaymentFailedException.class)
    public ResponseEntity<ErrorResponse> handlePaymentFailedException(PaymentFailedException e, HttpServletRequest request) {
        log.error(e.getMessage());

        return buildErrorResponse(e, HttpStatus.BAD_REQUEST, request);
    }

    private Throwable unwrap(Throwable e) {
        return Optional.of(org.springframework.core.NestedExceptionUtils.getMostSpecificCause(e))
                .orElse(e);
    }

    private ResponseEntity<ErrorResponse> buildErrorResponse (Exception e, HttpStatus status, HttpServletRequest request) {
        ErrorResponse response = new ErrorResponse(
                status.value(),
                status.getReasonPhrase(),
                e.getMessage(),
                request.getRequestURI(),
                LocalDateTime.now()
        );

        return ResponseEntity.status(status).body(response);
    }
}
