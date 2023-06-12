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
public class PostListResponseDto {

    private Long postId;

    private String writer;

    private String title;

    private Long likeCount;

    public PostListResponseDto(Post post){
        this.postId = post.getId();
        this.writer = post.getUser().getName();
        this.title = post.getTitle();
        this.likeCount = Long.valueOf(post.getLikeCount());


    }

}
