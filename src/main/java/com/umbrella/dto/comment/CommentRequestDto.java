package com.umbrella.dto.comment;

import lombok.Getter;

import javax.validation.constraints.NotBlank;

@Getter
public class CommentRequestDto {

    @NotBlank
    private String content;
    @NotBlank
    private String nickName;
    @NotBlank
    private int pageNumber;

}
