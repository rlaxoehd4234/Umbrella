package com.umbrella.controller;

import com.umbrella.dto.post.*;
import com.umbrella.service.Impl.PostServiceImpl;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@Getter
@RequiredArgsConstructor
@RequestMapping("/posts")
public class PostController {

    private final PostServiceImpl postService;

    @PostMapping
    public ResponseEntity<?> save(@Valid @RequestBody PostSaveRequestDto requestDto) { // 게시물 & 댓글 가져오기
        return ResponseEntity.ok().body(postService.save(requestDto));
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> update(@PathVariable Long id, @Validated PostUpdateRequestDto requestDto){
        return ResponseEntity.ok().body(postService.update(id, requestDto));
    }
    @GetMapping("/{id}")
    public ResponseEntity<?> findById(@PathVariable Long id){
        return ResponseEntity.ok().body(postService.findById(id));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable Long id){
        return ResponseEntity.ok().body(postService.delete(id));
    }

    @GetMapping
    public ResponseEntity<?> findPosts(@PageableDefault(size = 10) Pageable pageable){
        return ResponseEntity.ok().body(postService.findAllPosts(pageable));
    }

    @GetMapping("/search") // 검색 타이틀 검색
    public ResponseEntity<?> searchPost(@RequestParam("title") String title, @PageableDefault(size = 10) Pageable pageable){
        return ResponseEntity.ok().body(postService.findSearchPost(title ,pageable));
    }
    //추후 검색 기능 추가시 추가..


}
