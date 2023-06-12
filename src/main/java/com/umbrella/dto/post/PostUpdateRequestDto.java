package com.umbrella.dto.post;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.lang.Nullable;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.util.List;

@Getter
@NoArgsConstructor
public class PostUpdateRequestDto {

    @NotBlank
    @Size(max = 30)
    private String title;

    @NotBlank
    @Size(min =30, max = 1000)
    private String content;


    // 새롭게 추가된 이미지
    @Nullable
    private List<String> addedFileNameList;

    // 삭제된 이미지
    @Nullable
    private List<String> deletedFileNameList;


    @Builder
    public PostUpdateRequestDto(String title, String content){
        this.title = title;
        this.content = content;
    }


}
