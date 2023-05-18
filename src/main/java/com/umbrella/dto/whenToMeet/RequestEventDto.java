package com.umbrella.dto.whenToMeet;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.umbrella.domain.WorkSpace.WorkspaceUser;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Date;

@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class RequestEventDto {

    @Getter
    private Long workspaceId;

    @Getter
    private String title;

    @Getter
    private Date startDate;

    @Getter
    private Date endDate;
}
