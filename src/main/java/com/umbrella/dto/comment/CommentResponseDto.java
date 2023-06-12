package com.umbrella.dto.comment;

import lombok.*;


@NoArgsConstructor
@Getter
public class CommentResponseDto {

    private Long commentId;
    private String content;
    private String nickname; // userName -> 작성자

    @Builder
    public CommentResponseDto(Long commentId, String content, String nickname) {
        this.commentId = commentId;
        this.content = content;
        this.nickname = nickname;
    }
}
