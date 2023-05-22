package com.umbrella.service.Impl;

import com.umbrella.domain.Comment.Comment;
import com.umbrella.domain.Comment.CommentRepository;
import com.umbrella.domain.Post.Post;
import com.umbrella.domain.Post.PostRepository;
import com.umbrella.domain.User.User;
import com.umbrella.domain.User.UserRepository;
import com.umbrella.domain.exception.*;
import com.umbrella.dto.comment.CommentRequestDto;
import com.umbrella.dto.comment.CommentResponseDto;
import com.umbrella.security.utils.SecurityUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
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
    public List<CommentResponseDto> findComments(Integer pageNumber, Long postId){
        PageRequest pageRequest = makePageRequest(pageNumber);
        return returnResponseDtoList(pageRequest, postId);
    }

    // 댓글 생성
    public List<CommentResponseDto> create(CommentRequestDto commentRequestDto, Long postId){

        User user = userRepository.findById(securityUtil.getLoginUserId())
                .orElseThrow(() -> new UserException(UserExceptionType.NOT_FOUND_ERROR));
        // 생성 검증 완료 -> 로그인하지 않은 유저는~ 으로 변경할 필요가 있음


        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new PostException(PostExceptionType.NOT_FOUND_POST));


        Comment comment = Comment.builder()
                .content(commentRequestDto.getContent())
                .user(user)
                .post(post)
                .build();

        Comment savedComment = commentRepository.save(comment);

        return findComments(commentRequestDto.getPageNumber(), savedComment.getPost().getId());
    }


    // 댓글 수정
    // post/{post-id}/comments/{comment-id}
    public List<CommentResponseDto> update(CommentRequestDto commentRequestDto, Long postId, Long commentId){

        Comment comment = validateComment(commentId);
        validateUser(comment.getUser()); // 유저 검증

        comment.update(commentRequestDto.getContent());
        commentRepository.save(comment);

        return findComments(commentRequestDto.getPageNumber(), postId);
    }

    //댓글 삭제
    //  post/{post-id}/comments/{comment-id}
    public List<CommentResponseDto> delete(CommentRequestDto commentRequestDto, Long postId, Long commentId){

        Comment comment = validateComment(commentId);

        validateUser(comment.getUser()); // 삭제 검증 완료
        commentRepository.delete(comment);

        return findComments(commentRequestDto.getPageNumber(), postId);
    }


    private PageRequest makePageRequest(Integer pageNumber){ // PageRequest Integer 도 지원해줌
        return PageRequest
                .of(pageNumber, 10, Sort.by(Sort.Direction.ASC, "createDate"));
    }

    @Transactional(readOnly = true)
    public List<CommentResponseDto> returnResponseDtoList(PageRequest pageRequest, Long postId){
        List<Comment> commentList = commentRepository.findAllByPost_Id(pageRequest, postId);

        List<CommentResponseDto> responseDtoList = new ArrayList<>();

        // DTO 변환
        for (Comment commentEntity : commentList) {

            CommentResponseDto commentResponseDto =  CommentResponseDto.builder()
                    .commentId(commentEntity.getId())
                    .content(commentEntity.getContent())
                    .nickname(commentEntity.getUser().getNickName())
                    .build();

            responseDtoList.add(commentResponseDto);

        }

        return  responseDtoList;
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
