package com.umbrella.dto.comment;

import lombok.Builder;
import lombok.ToString;

@ToString
@Builder
public class CommentResponseDto {

    private Long commentId;
    private String content;
    private String createDate;
    private String nickname; // userName -> 작성자


}
