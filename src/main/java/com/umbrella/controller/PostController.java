package com.umbrella.project_umbrella.controller;

import com.umbrella.project_umbrella.dto.post.PostListResponseDto;
import com.umbrella.project_umbrella.dto.post.PostResponseDto;
import com.umbrella.project_umbrella.dto.post.PostSaveRequestDto;
import com.umbrella.project_umbrella.dto.post.PostUpdateRequestDto;
import com.umbrella.project_umbrella.service.PostService;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@Getter
@RequiredArgsConstructor
@RequestMapping("/posts")
public class PostController {

    private final PostService postService;

    @PostMapping
    public Long save(@Valid @RequestBody PostSaveRequestDto requestDto){ // 게시물 & 댓글 가져오기
        return postService.save(requestDto);
    }

    @PutMapping("/{id}")
    public Long update(@PathVariable Long id, @Validated PostUpdateRequestDto requestDto){
        return postService.update(id, requestDto);
    }
    @GetMapping("/{id}")
    public PostResponseDto findById(@PathVariable Long id){
        return postService.findById(id);
    }

    @DeleteMapping("/{id}")
    public Long delete(@PathVariable Long id){
        return postService.delete(id);
    }

    @GetMapping
    public Page<PostListResponseDto> findPosts(@PageableDefault(size = 10) Pageable pageable){
        return postService.findAllPosts(pageable);
    }

    @GetMapping("/search") // 검색 타이틀 검색
    public Page<PostListResponseDto> searchPost(@RequestParam("title") String title, @PageableDefault(size = 10) Pageable pageable){
        return postService.findSearchPost(title ,pageable);
    }
    //추후 검색 기능 추가시 추가..
    

}
