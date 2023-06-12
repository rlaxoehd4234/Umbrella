package com.umbrella.exception;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ExceptionDto {
        private int errorCode;

        private String errorMessage;
}
