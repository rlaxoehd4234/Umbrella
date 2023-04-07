package com.umbrella.project_umbrella.dto.post;

import com.umbrella.project_umbrella.domain.Post.Post;
import com.umbrella.project_umbrella.domain.User.User;
import lombok.Builder;
import lombok.Getter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Getter
public class PostSaveRequestDto {


    @NotBlank
    private String title;
    @NotBlank
    @Size(min = 30, max = 1000)
    private String content;
    @NotBlank
    private String writer;


    @Builder
    public PostSaveRequestDto(String title, String content, String writer){
        this.title = title;
        this.content = content;
        this.writer = writer;
    }

    public Post toEntity(){
        return Post.builder().
                title(title).
                content(content).
                writer(writer).
                build();
    }

}
