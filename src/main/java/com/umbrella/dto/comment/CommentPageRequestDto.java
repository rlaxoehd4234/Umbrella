package com.umbrella.dto.comment;

import lombok.Data;
import lombok.Getter;

import javax.validation.constraints.NotNull;

@Getter
public class CommentPageRequestDto {

    @NotNull
    private Integer pageNumber;
    // Notnull Valid @을 달기 위해 Integer로 작성
}
