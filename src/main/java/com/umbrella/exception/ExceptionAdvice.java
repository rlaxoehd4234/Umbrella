package com.umbrella.exception;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.umbrella.domain.exception.UserException;
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
import static com.umbrella.domain.exception.WorkspaceExceptionType.*;

@RestControllerAdvice
public class ExceptionAdvice {

    private static final String EMAIL_BLANK_ERROR_MESSAGE = "email must not be blank";
    private static final String NICKNAME_BLANK_ERROR_MESSAGE = "nickName must not be blank";
    private static final String PASSWORD_BLANK_ERROR_MESSAGE = "password must not be blank";
    private static final String NAME_BLANK_ERROR_MESSAGE = "mName must not be blank";
    private static final String BIRTHDATE_BLANK_ERROR_MESSAGE = "birthDate must not be null";
    private static final String GENDER_BLANK_ERROR_MESSAGE = "gender must not be blank";

    private static final String WORKSPACE_TITLE_BLANK_ERROR_MESSAGE = "workspace_title must not be blank";
    private static final String WORKSPACE_DESCRIPTION_BLANK_ERROR_MESSAGE = "workspace_description must not be blank";
    private static final String ALREADY_ENTERED_WORKSPACE_ERROR_MESSAGE = "이미 입장한 워크스페이스 입니다.";

    @ExceptionHandler(UserException.class)
    public ResponseEntity MainExceptionHandler(UserException exception){

        return new ResponseEntity(new ExceptionDto(exception.getBaseExceptionType().getErrorCode(),
                exception.getBaseExceptionType().getErrorMessage()),
                exception.getBaseExceptionType().getHttpStatus());
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity IllegalArgumentHandler(IllegalArgumentException exception) {
        Map<String, BaseExceptionType> errorMap = new HashMap<>();
        /* 유저 회원가입 발생 오류 */
        errorMap.put(EMAIL_BLANK_ERROR_MESSAGE, BLANK_EMAIL_ERROR);
        errorMap.put(NICKNAME_BLANK_ERROR_MESSAGE, BLANK_NICKNAME_ERROR);
        errorMap.put(PASSWORD_BLANK_ERROR_MESSAGE, BLANK_PASSWORD_ERROR);
        errorMap.put(NAME_BLANK_ERROR_MESSAGE, BLANK_NAME_ERROR);
        errorMap.put(BIRTHDATE_BLANK_ERROR_MESSAGE, BLANK_BIRTHDATE_ERROR);
        errorMap.put(GENDER_BLANK_ERROR_MESSAGE, BLANK_GENDER_ERROR);

        /* 워크스페이스 생성 발생 오류 */
        errorMap.put(WORKSPACE_TITLE_BLANK_ERROR_MESSAGE, BLANK_WORKSPACE_TITLE_ERROR);
        errorMap.put(WORKSPACE_DESCRIPTION_BLANK_ERROR_MESSAGE, BLANK_WORKSPACE_DESCRIPTION_ERROR);

        /* 워크스페이스 입장 발생 오류 */
        errorMap.put(ALREADY_ENTERED_WORKSPACE_ERROR_MESSAGE, ALREADY_ENTERED_WORKSPACE_ERROR);
        BaseExceptionType exceptionMap = errorMap.getOrDefault(exception.getMessage(), DEFAULT_USER_ERROR);

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
