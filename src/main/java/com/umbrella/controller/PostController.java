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
@RequestMapping
public class PostController {

    private final PostServiceImpl postService;

    @PostMapping("/{board_id}/post")
    public ResponseEntity<Long> save(@PathVariable Long board_id, @Valid @RequestBody PostSaveRequestDto requestDto) { // 게시물 & 댓글 가져오기
        return ResponseEntity.ok().body(postService.save(board_id,requestDto));
    }

    @PutMapping("/{board_id}/{post_id}")
    public ResponseEntity<Long> update(@PathVariable Long board_id,@PathVariable Long post_id, @Validated PostUpdateRequestDto requestDto){
        return ResponseEntity.ok().body(postService.update(board_id, post_id, requestDto));
    }
    @GetMapping("/{board_id}/{post_id}")
    public ResponseEntity<PostResponseDto> findById(@PathVariable Long board_id, @PathVariable Long post_id){
        return ResponseEntity.ok().body(postService.findById(board_id,post_id));
    }

    @DeleteMapping("/{board_id}/{post_id}/delete")
    public ResponseEntity<Long> delete(@PathVariable Long board_id, @PathVariable Long post_id){
        return ResponseEntity.ok().body(postService.delete(board_id,post_id));
    }

    @GetMapping("/{board_id}/posts")
    public ResponseEntity<Page<PostListResponseDto>> findPosts(@PathVariable Long board_id, @PageableDefault(size = 10) Pageable pageable){
        return ResponseEntity.ok().body(postService.findAllPosts(board_id, pageable));
    }

    @GetMapping("/search") // 검색 타이틀 검색
    public ResponseEntity<?> searchPost(@RequestParam("title") String title, @PageableDefault(size = 10) Pageable pageable){
        return ResponseEntity.ok().body(postService.findSearchPost(title ,pageable));
    }


    //추후 검색 기능 추가시 추가..


}
