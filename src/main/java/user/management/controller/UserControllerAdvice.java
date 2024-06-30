package user.management.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.ObjectError;
import org.springframework.validation.method.MethodValidationException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;
import user.management.dto.ErrorDto;
import user.management.exception.UserAlreadyExistsException;
import user.management.exception.UserNotFoundException;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;

@RestControllerAdvice
@Slf4j
public class UserControllerAdvice extends ResponseEntityExceptionHandler {

    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex, HttpHeaders headers,
                                                                  HttpStatusCode status, WebRequest request) {
        log.warn(ex.getLocalizedMessage(), ex);
        Optional<ObjectError> error = ex.getAllErrors().stream().findFirst();
        return ResponseEntity.badRequest().body(
                ErrorDto.builder()
                        .timestamp(LocalDateTime.now())
                        .status(HttpStatus.BAD_REQUEST.value())
                        .message(error.isPresent() ? error.get().getDefaultMessage() : "")
                        .error(ex.getLocalizedMessage())
                        .path(request.getDescription(false))
                        .build()
        );
    }

    /*@Override
    protected ResponseEntity<Object> handleMethodValidationException(MethodValidationException ex, HttpHeaders headers,
                                                                     HttpStatus status, WebRequest request) {
        return super.handleMethodValidationException(ex, headers, status, request);
    }*/

    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(DataIntegrityViolationException.class)
    protected ErrorDto handleDataIntegrityViolationException(DataIntegrityViolationException ex, WebRequest request) {
        log.error(ex.getLocalizedMessage(), ex);
        return ErrorDto.builder()
                .timestamp(LocalDateTime.now())
                .error(HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase())
                .status(HttpStatus.INTERNAL_SERVER_ERROR.value())
                .message(ex.getLocalizedMessage())
                .path(request.getDescription(false))
                .build();
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(UserNotFoundException.class)
    protected ErrorDto handleUserNotFoundException(UserNotFoundException ex, WebRequest request) {
        log.warn(ex.getLocalizedMessage(), ex);
        return ErrorDto.builder()
                .timestamp(LocalDateTime.now())
                .error(HttpStatus.BAD_REQUEST.getReasonPhrase())
                .status(HttpStatus.BAD_REQUEST.value())
                .message(ex.getLocalizedMessage())
                .path(request.getDescription(false))
                .build();
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(UserAlreadyExistsException.class)
    protected ErrorDto handleUserAlreadyExistsException(UserAlreadyExistsException ex, WebRequest request) {
        log.warn(ex.getLocalizedMessage(), ex);
        return ErrorDto.builder()
                .timestamp(LocalDateTime.now())
                .error(HttpStatus.BAD_REQUEST.getReasonPhrase())
                .status(HttpStatus.BAD_REQUEST.value())
                .message(ex.getLocalizedMessage())
                .path(request.getDescription(false))
                .build();
    }
}
