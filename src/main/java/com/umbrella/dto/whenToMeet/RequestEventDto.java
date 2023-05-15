package com.umbrella.dto.whenToMeet;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.List;

@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class RequestEventDto {

    @Getter
    private String title;

    @Getter
    private Date startDate;

    @Getter
    private Date endDate;

    @Getter
    private List<String> members;
}
