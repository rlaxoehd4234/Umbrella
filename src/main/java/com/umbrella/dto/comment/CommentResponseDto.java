package com.umbrella.dto.comment;

import com.umbrella.domain.Comment.Comment;
import lombok.*;


@NoArgsConstructor
@Getter
public class CommentResponseDto {

    private Long commentId;
    private String content;
    private String nickName; // userName -> 작성자



    public CommentResponseDto(Comment comment) {
        this.commentId = comment.getId();
        this.content = comment.getContent();
        this.nickName = comment.getUser().getNickName();
    }
}
