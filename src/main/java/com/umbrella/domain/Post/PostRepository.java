package com.umbrella.project_umbrella.domain.Post;

import com.umbrella.project_umbrella.dto.post.PostListResponseDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface PostRepository extends JpaRepository<Post,Long> {

    Page<Post> findAll(Pageable pageable);
    Page<Post> findByTitleContaining(String title, Pageable pageable);
}
