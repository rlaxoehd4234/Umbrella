package com.umbrella.domain.Post;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;


public interface PostRepository extends JpaRepository<Post,Long> {

    Page<Post> findAllByBoardId(Long board_id, Pageable pageable);
    Page<Post> findByTitleContaining(String title, Pageable pageable);
}
