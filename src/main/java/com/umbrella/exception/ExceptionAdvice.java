package com.umbrella.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.ArrayList;
import java.util.List;

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
    public ResponseEntity DtoIllegalArgumentHandler(IllegalArgumentException exception){
        switch (exception.getMessage()) {
            case (EMAIL_ERROR_MESSAGE):
                return new ResponseEntity(new ExceptionDto(BLANK_EMAIL_ERROR.getErrorCode(), BLANK_EMAIL_ERROR.getErrorMessage()),
                        BLANK_EMAIL_ERROR.getHttpStatus());
            case (NICKNAME_ERROR_MESSAGE):
                return new ResponseEntity(new ExceptionDto(BLANK_NICKNAME_ERROR.getErrorCode(), BLANK_NICKNAME_ERROR.getErrorMessage()),
                        BLANK_NICKNAME_ERROR.getHttpStatus());
            case (PASSWORD_ERROR_MESSAGE):
                return new ResponseEntity(new ExceptionDto(BLANK_PASSWORD_ERROR.getErrorCode(), BLANK_PASSWORD_ERROR.getErrorMessage()),
                        BLANK_PASSWORD_ERROR.getHttpStatus());
            case (NAME_ERROR_MESSAGE):
                return new ResponseEntity(new ExceptionDto(BLANK_NAME_ERROR.getErrorCode(), BLANK_NAME_ERROR.getErrorMessage()),
                        BLANK_NAME_ERROR.getHttpStatus());
            case (BIRTHDATE_ERROR_MESSAGE):
                return new ResponseEntity(new ExceptionDto(BLANK_BIRTHDATE_ERROR.getErrorCode(), BLANK_BIRTHDATE_ERROR.getErrorMessage()),
                        BLANK_BIRTHDATE_ERROR.getHttpStatus());
            case (GENDER_ERROR_MESSAGE):
                return new ResponseEntity(new ExceptionDto(BLANK_GENDER_ERROR.getErrorCode(), BLANK_GENDER_ERROR.getErrorMessage()),
                        BLANK_GENDER_ERROR.getHttpStatus());
            default:
                return new ResponseEntity(new ExceptionDto(699, "잘못된 인자가 삽입되었습니다."), HttpStatus.BAD_REQUEST);
        }
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Object> handleValidationException(MethodArgumentNotValidException ex) {
        BindingResult bindingResult = ex.getBindingResult();
        List<String> errorMessages = new ArrayList<>();

        for (FieldError error : bindingResult.getFieldErrors()) {
            errorMessages.add(error.getField() + " : " + error.getDefaultMessage());
        }

        ExceptionDto errorResponse = new ExceptionDto(HttpStatus.BAD_REQUEST.value(), String.valueOf(errorMessages));

        return ResponseEntity.badRequest().body(errorResponse);
    }
}
