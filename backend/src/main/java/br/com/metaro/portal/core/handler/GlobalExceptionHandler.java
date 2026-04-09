package br.com.metaro.portal.core.handler;

import br.com.metaro.portal.core.dto.errors.CustomErrorDto;
import br.com.metaro.portal.core.dto.errors.ErrorDto;
import br.com.metaro.portal.core.dto.errors.FieldMessageDto;
import br.com.metaro.portal.core.services.exceptions.ForbiddenException;
import br.com.metaro.portal.core.services.exceptions.ResourceNotFoundException;
import br.com.metaro.portal.core.services.exceptions.UnprocessableEntityException;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ErrorDto> resourceNotFound(ResourceNotFoundException e, HttpServletRequest request) {
        HttpStatus status = HttpStatus.NOT_FOUND;
        ErrorDto error = new ErrorDto(Instant.now(), status.value(), e.getMessage(), request.getRequestURI());
        return ResponseEntity.status(status).body(error);
    }

    @ExceptionHandler(UnprocessableEntityException.class)
    public ResponseEntity<ErrorDto> unprocessableEntity(UnprocessableEntityException e, HttpServletRequest request) {
        HttpStatus status = HttpStatus.UNPROCESSABLE_ENTITY;
        ErrorDto error = new ErrorDto(Instant.now(), status.value(), e.getMessage(), request.getRequestURI());
        return ResponseEntity.status(status).body(error);
    }

    @ExceptionHandler(ForbiddenException.class)
    public ResponseEntity<ErrorDto> forbidden(ForbiddenException e, HttpServletRequest request) {
        HttpStatus status = HttpStatus.FORBIDDEN;
        ErrorDto error = new ErrorDto(Instant.now(), status.value(), e.getMessage(), request.getRequestURI());
        return ResponseEntity.status(status).body(error);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<CustomErrorDto> methodArgumentNotValid(MethodArgumentNotValidException e, HttpServletRequest request) {
        HttpStatus status = HttpStatus.NOT_ACCEPTABLE;

        List<FieldMessageDto> errors = new ArrayList<>();
        for (FieldError fieldError : e.getFieldErrors()) {
            errors.add(new FieldMessageDto(fieldError.getField(), fieldError.getDefaultMessage()));
        }

        CustomErrorDto error = new CustomErrorDto(Instant.now(), status.value(), errors, request.getRequestURI());
        return ResponseEntity.status(status).body(error);
    }
}
