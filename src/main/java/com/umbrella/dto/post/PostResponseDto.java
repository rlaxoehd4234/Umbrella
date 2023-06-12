package com.umbrella.dto.post;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.umbrella.domain.Post.Post;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class PostResponseDto {

    private Long postId;
    private String title;
    private String writer;
    private String content;
    private Integer likeCount;


    public PostResponseDto(Post post){
        this.postId = post.getId();
        this.title = post.getTitle();
        this.writer = post.getUser().getName();
        this.content = post.getContent();
        this.likeCount = getLikeCount();
    }


}
