package com.umbrella.service;

import com.umbrella.dto.Heart.HeartRequestDto;
import com.umbrella.dto.Heart.HeartResponseDto;

public interface HeartService {
    void insert(HeartRequestDto heartRequestDto);
    void delete(HeartRequestDto heartRequestDto);

}
