package com.umbrella.domain.PostImg;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;


public interface PostImgRepository extends JpaRepository<PostImg, Long> {

    List<PostImg> findAllByPostId(Long postId);

    List<PostImg> findAllByBoard_Id(Long boardId);

    List<PostImg> findAllByWorkSpaceId(Long workspaceId);

    void deleteByImgName (String imageName);

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query(value = "DELETE FROM PostImg pi WHERE pi.post.id =:postId")
    void deleteAllByPostId(@Param("postId")Long postId);

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query(value = "DELETE FROM PostImg pi WHERE pi.board.id =:boardId")
    void deleteAllByBoardId(@Param("boardId") Long boardId);

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query(value = "DELETE FROM PostImg pi WHERE pi.workSpace.id =:workSpaceId")
    void deleteAllByWorkSpaceId(@Param("workSpaceId") Long workSpaceId);

}
