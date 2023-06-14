package com.umbrella.service.Impl;

import com.umbrella.domain.Board.Board;
import com.umbrella.domain.Comment.Comment;
import com.umbrella.domain.Comment.CommentRepository;
import com.umbrella.domain.Post.Post;
import com.umbrella.domain.Post.PostRepository;
import com.umbrella.domain.User.User;
import com.umbrella.domain.User.UserRepository;
import com.umbrella.domain.exception.*;
import com.umbrella.dto.comment.CommentDeleteDto;
import com.umbrella.dto.comment.CommentRequestDto;
import com.umbrella.dto.comment.CommentResponseDto;
import com.umbrella.dto.comment.CommentUpdateDto;
import com.umbrella.security.utils.SecurityUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import java.util.ArrayList;
import java.util.List;
import java.util.Objects;


@Service
@RequiredArgsConstructor
@Transactional
public class CommentServiceImpl {

    private final CommentRepository commentRepository;
    private final PostRepository postRepository;
    private final UserRepository userRepository;
    private final SecurityUtil securityUtil;


    // 댓글 조회
    // /post/{post-id}/comments
    @Transactional(readOnly = true)
    public Page<CommentResponseDto> findAllComments(Long postId, Pageable pageable){
        Page<Comment> page = commentRepository.findAllByPost_Id(pageable, postId);

        return page.map(CommentResponseDto::new);
    }

    public Page<CommentResponseDto> createComment(Long postId, CommentRequestDto dto, Pageable pageable){
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new PostException(PostExceptionType.NOT_FOUND_POST));
        User findUser = userRepository.findById(securityUtil.getLoginUserId())
                .orElseThrow(() -> new UserException(UserExceptionType.NOT_FOUND_ERROR));

        Comment comment = Comment.builder()
                .post(post)
                .user(findUser)
                .content(dto.getContent())
                .build();
        commentRepository.save(comment);

        return findAllComments(postId, pageable);
    }

    public Page<CommentResponseDto> updateComment(Long postId, CommentUpdateDto dto, Pageable pageable){
        User findUser = userRepository.findById(securityUtil.getLoginUserId())
                .orElseThrow(() -> new UserException(UserExceptionType.NOT_FOUND_ERROR));

        validateUser(findUser);

        Comment comment = validateComment(dto.getCommentId());
        validateUser(comment.getUser()); // 유저 검증

        comment.update(dto.getContent());

        return findAllComments(postId, pageable);
    }

    public Page<CommentResponseDto> deleteComment(Long postId, CommentDeleteDto commentDeleteDto, Pageable pageable){

        User findUser = userRepository.findById(securityUtil.getLoginUserId())
                .orElseThrow(() -> new UserException(UserExceptionType.NOT_FOUND_ERROR));

        validateUser(findUser);

        Comment comment = validateComment(commentDeleteDto.getCommentId());
        validateUser(comment.getUser()); // 유저 검증

        commentRepository.delete(comment);

        return findAllComments(postId, pageable);
    }





    // 생성 권한

    // 삭제 수정 권한
    private void validateUser(User user){
        if(!Objects.equals(user.getId(), securityUtil.getLoginUserId())){
            throw new UserException(UserExceptionType.UN_AUTHORIZE_ERROR);
        }
    }

    // 엔티티 없는 경우 에러 던지기
    private Comment validateComment(Long commentId){
        return commentRepository.findById(commentId)
                .orElseThrow(() -> new CommentException(CommentExceptionType.NOT_FOUND_COMMENT));
    }




}
