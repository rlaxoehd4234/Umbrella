package com.umbrella.constant;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import com.umbrella.domain.exception.UserException;
import lombok.Getter;

import static com.umbrella.domain.exception.UserExceptionType.UNSUPPORTED_GENDER_ERROR;

public enum Gender {
    MALE("MALE"), FEMALE("FEMALE"), UNKNOWN("UNKNOWN");

    private String genderValue;

   Gender(String genderValue) {
        this.genderValue = genderValue;
    }

    @JsonValue
    public String getGenderValue() {
       return this.genderValue;
    }

    @JsonCreator
    public Gender from(String genderValue) {
        for (Gender status : Gender.values()) {
            if (status.getGenderValue().equals(genderValue)) {
                return status;
            }
        }
        throw new UserException(UNSUPPORTED_GENDER_ERROR);
    }
}
