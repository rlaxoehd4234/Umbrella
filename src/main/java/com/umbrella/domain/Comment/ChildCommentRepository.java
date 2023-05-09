package com.umbrella.domain.Comment;

import org.springframework.data.jpa.repository.JpaRepository;

public interface ChildCommentRepository extends JpaRepository<ChildComment, Long> {


}
