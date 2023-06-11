package com.umbrella.service;

import com.umbrella.dto.post.PostListResponseDto;
import com.umbrella.dto.post.PostResponseDto;
import com.umbrella.dto.post.PostSaveRequestDto;
import com.umbrella.dto.post.PostUpdateRequestDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface PostService {

    Long save(Long board_id, PostSaveRequestDto requestDto) throws IllegalAccessException;

    Long update(Long board_id, Long post_id, PostUpdateRequestDto requestDto);

    Long delete(Long board_id, Long post_id);

    PostResponseDto findById(Long board_id, Long post_id);

    Page<PostListResponseDto> findAllPosts(Pageable pageable);

    Page<PostListResponseDto> findSearchPost(String title, Pageable pageable);
}
