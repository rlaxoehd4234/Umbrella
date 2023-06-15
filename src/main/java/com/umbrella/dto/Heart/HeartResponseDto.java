package com.umbrella.dto.Heart;

import com.umbrella.domain.Heart.PostHeart;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class HeartResponseDto {

    private String status;

    enum likeStatus{
        LIKE,UNLIKE;
    }

    public HeartResponseDto(Boolean key) {
        if (key) this.status = String.valueOf(likeStatus.LIKE);

        else this.status = String.valueOf(likeStatus.UNLIKE);
    }



}
