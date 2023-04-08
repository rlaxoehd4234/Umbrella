package com.umbrella.dto.post;

import com.umbrella.domain.Post.Post;
import lombok.Getter;

import java.util.List;

@Getter
public class PostResponseDto {

    private Long id;
    private String title;

    private String writer;

    private String content;


    public PostResponseDto(Post post){
        this.id = post.getId();
        this.title = post.getTitle();
        this.writer = post.getWriter();
        this.content = post.getContent();
    }


}
