package com.umbrella.service;

import com.umbrella.dto.post.PostListResponseDto;
import com.umbrella.dto.post.PostResponseDto;
import com.umbrella.dto.post.PostSaveRequestDto;
import com.umbrella.dto.post.PostUpdateRequestDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface PostService {

    Long save(PostSaveRequestDto requestDto);

    Long update(Long id, PostUpdateRequestDto requestDto);

    Long delete(Long id);

    PostResponseDto findById(Long id);

    Page<PostListResponseDto> findAllPosts(Pageable pageable);

    Page<PostListResponseDto> findSearchPost(String title, Pageable pageable);
}
