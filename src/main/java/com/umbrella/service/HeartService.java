package com.umbrella.service;

import com.umbrella.dto.Heart.HeartRequestDto;

public interface HeartService {
    void insert(HeartRequestDto heartRequestDto);
    void delete(HeartRequestDto heartRequestDto);

}
