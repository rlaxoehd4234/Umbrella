package com.umbrella.controller;

import com.umbrella.dto.Heart.HeartRequestDto;
import com.umbrella.service.HeartService;
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
    public ResponseEntity<?> insert(@RequestBody @Valid HeartRequestDto heartRequestDto){
        heartService.insert(heartRequestDto);
        return ResponseEntity.ok().body("완료되었습니다.");
    }
    @DeleteMapping("/post/heart")
    public ResponseEntity<?> delete(@RequestBody @Valid HeartRequestDto heartRequestDto){
        heartService.delete(heartRequestDto);
        return ResponseEntity.ok().body("완료되었습니다.");
    }
}
