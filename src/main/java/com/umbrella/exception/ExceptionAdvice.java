package com.umbrella.exception;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.umbrella.domain.exception.UserException;
import com.umbrella.domain.exception.UserExceptionType;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.umbrella.domain.exception.UserExceptionType.*;

@RestControllerAdvice
public class ExceptionAdvice {

    private static final String EMAIL_ERROR_MESSAGE = "email must not be blank";
    private static final String NICKNAME_ERROR_MESSAGE = "nickName must not be blank";
    private static final String PASSWORD_ERROR_MESSAGE = "password must not be blank";
    private static final String NAME_ERROR_MESSAGE = "mName must not be blank";
    private static final String BIRTHDATE_ERROR_MESSAGE = "birthDate must not be null";
    private static final String GENDER_ERROR_MESSAGE = "gender must not be blank";

    @ExceptionHandler
    public ResponseEntity MainExceptionHandler(BaseException exception){

        return new ResponseEntity(new ExceptionDto(exception.getBaseExceptionType().getErrorCode(), exception.getBaseExceptionType().getErrorMessage()),
                exception.getBaseExceptionType().getHttpStatus());
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity DtoIllegalArgumentHandler(IllegalArgumentException exception) {
        Map<String, UserExceptionType> errorMap = new HashMap<>();
        errorMap.put(EMAIL_ERROR_MESSAGE, BLANK_EMAIL_ERROR);
        errorMap.put(NICKNAME_ERROR_MESSAGE, BLANK_NICKNAME_ERROR);
        errorMap.put(PASSWORD_ERROR_MESSAGE, BLANK_PASSWORD_ERROR);
        errorMap.put(NAME_ERROR_MESSAGE, BLANK_NAME_ERROR);
        errorMap.put(BIRTHDATE_ERROR_MESSAGE, BLANK_BIRTHDATE_ERROR);
        errorMap.put(GENDER_ERROR_MESSAGE, BLANK_GENDER_ERROR);

        UserExceptionType exceptionMap = errorMap.getOrDefault(exception.getMessage(), DEFAULT_ERROR);

        return ResponseEntity.status(exceptionMap.getHttpStatus())
                .body(new ExceptionDto(exceptionMap.getErrorCode(), exceptionMap.getErrorMessage()));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Object> handleValidationException(MethodArgumentNotValidException exception) {
        BindingResult bindingResult = exception.getBindingResult();
        List<String> errorMessages = new ArrayList<>();

        for (FieldError error : bindingResult.getFieldErrors()) {
            errorMessages.add(error.getDefaultMessage());
        }

        Map<String, Object> errorResponse = new HashMap<>();
        errorResponse.put("errorCode", HttpStatus.BAD_REQUEST.value());
        errorResponse.put("errorMessage", errorMessages);

        try {
            String responseBody = new ObjectMapper().writeValueAsString(errorResponse);
            return ResponseEntity.badRequest().body(responseBody);
        } catch (JsonProcessingException ex) {
            // TODO : Exception Handling
            return ResponseEntity.badRequest().body(ex.getMessage());
        }
    }
}
