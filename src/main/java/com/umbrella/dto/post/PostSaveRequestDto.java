package com.umbrella.dto.post;

import com.umbrella.domain.Post.Post;
import com.umbrella.domain.User.User;
import lombok.Builder;
import lombok.Getter;
import org.springframework.lang.Nullable;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.util.ArrayList;
import java.util.List;

@Getter
public class PostSaveRequestDto {
    @NotBlank
    private String title;
    @NotBlank
    @Size(min = 30, max = 1000)
    private String content;

    @Nullable
    private List<String> fileNameList;

    @Builder
    public PostSaveRequestDto(String title, String content,String writer , String boardName){
        this.title = title;
        this.content = content;
    }

    public Post toEntity(){
        return Post.builder().
                title(title).
                content(content).
                build();
    }


}
