package com.umbrella.controller;

import com.umbrella.dto.Heart.HeartRequestDto;
import com.umbrella.dto.Heart.HeartResponseDto;
import com.umbrella.service.Impl.HeartServiceImpl;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@Getter
@RequiredArgsConstructor
@RequestMapping
public class PostHeartController {

    private final HeartServiceImpl heartService;
    @PostMapping("/post/heart")
    public ResponseEntity<HeartResponseDto> insert(@RequestBody @Valid HeartRequestDto heartRequestDto){
        heartService.insert(heartRequestDto);
        return ResponseEntity.ok().body(new HeartResponseDto("LIKED"));
    }
    @DeleteMapping("/post/heart")
    public ResponseEntity<HeartResponseDto> delete(@RequestBody @Valid HeartRequestDto heartRequestDto){
        heartService.delete(heartRequestDto);
        return ResponseEntity.ok().body(new HeartResponseDto("UNLIKED"));
    }
}
