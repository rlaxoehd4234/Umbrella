package com.umbrella.dto.img;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
public class ImgDto {

    private String imgName;
    private String imgUrl;

}
