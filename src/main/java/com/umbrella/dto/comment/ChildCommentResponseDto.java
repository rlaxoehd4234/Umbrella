package com.umbrella.dto.comment;

import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
public class ChildCommentResponseDto {

    private String content;
    private String nickName;

}
