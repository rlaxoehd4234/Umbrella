package com.umbrella.dto.comment;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;


@Data
@NoArgsConstructor
public class ChildCommentRequestDto {

    @NotNull
    private Long commentId;

    @NotBlank
    @Size(min = 1, max = 500, message = "1자 이상, 500자 이하만 가능합니다.")
    private String content;

    @NotNull
    private String nickname; // userName -> 작성자

}
