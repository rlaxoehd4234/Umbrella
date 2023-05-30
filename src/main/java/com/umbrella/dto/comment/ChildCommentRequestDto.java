package com.umbrella.dto.comment;

import lombok.Data;


@Data
public class ChildCommentRequestDto {

    private Long commentId;
    private String content;
    private String nickname; // userName -> 작성자

}
