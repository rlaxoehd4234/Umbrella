package com.umbrella.dto.post;

import com.umbrella.domain.Post.Post;
import com.umbrella.domain.User.User;
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
    private String boardName;



    @Builder
    public PostSaveRequestDto(String title, String content,String writer , String boardName){
        this.title = title;
        this.content = content;
        this.writer = writer;
        this.boardName = boardName;
    }

    public Post toEntity(){
        return Post.builder().
                title(title).
                content(content).
                writer(writer).
                build();
    }

}
