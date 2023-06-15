package com.umbrella.service;

import com.umbrella.dto.Heart.HeartRequestDto;
import com.umbrella.dto.Heart.HeartResponseDto;

public interface HeartService {
    HeartResponseDto insert(HeartRequestDto heartRequestDto);
    HeartResponseDto delete(HeartRequestDto heartRequestDto);

}
