package com.umbrella.project_umbrella.dto.post;

import lombok.Builder;
import lombok.Getter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Getter
public class PostUpdateRequestDto {

    @NotBlank
    @Size(max = 30)
    private String title;

    @NotBlank
    @Size(min =30, max = 1000)
    private String content;

    @Builder
    public PostUpdateRequestDto(String title, String content){
        this.title = title;
        this.content = content;
    }


}
