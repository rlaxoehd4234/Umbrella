package com.umbrella.dto.comment;

import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;

@Getter
@NoArgsConstructor
public class CommentDeleteDto {

    @NotNull
    private Long commentId;
}
