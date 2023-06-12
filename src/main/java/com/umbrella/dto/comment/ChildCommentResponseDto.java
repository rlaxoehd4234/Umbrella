package com.umbrella.dto.comment;

import lombok.Builder;
import lombok.NoArgsConstructor;

@Builder
public class ChildCommentResponseDto {

    private String content;
    private String nickName;

}
