package com.umbrella.dto.Heart;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class HeartResponseDto {

    private String status;


    public HeartResponseDto(String status){
        this.status = status;
    }
}
