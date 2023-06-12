package com.umbrella.domain.Comment;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ChildCommentRepository extends JpaRepository<ChildComment, Long> {

    List<ChildComment> findAllByParentComment_Id(Long parentId, Sort sort);
}
