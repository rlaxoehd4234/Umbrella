package com.umbrella.dto.email;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class EmailAuthResponseDto {
    @Getter
    private String authKey;

    @Builder
    public EmailAuthResponseDto(String authKey) {
        this.authKey = authKey;
    }
}
