package com.umbrella.service.Impl;

import com.umbrella.domain.Comment.Comment;
import com.umbrella.domain.Comment.CommentRepository;
import com.umbrella.domain.Post.Post;
import com.umbrella.domain.Post.PostRepository;
import com.umbrella.domain.User.User;
import com.umbrella.domain.User.UserRepository;
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
    public List<CommentResponseDto> findComments(CommentRequestDto commentRequestDto, Long postId){
        PageRequest pageRequest = makePageRequest(commentRequestDto);
        return returnResponseDtoList(pageRequest, postId);
    }

    // 댓글 생성
    public List<CommentResponseDto> create(Long postId, CommentRequestDto commentRequestDto){
        Long userId = securityUtil.getLoginUserId(); // 로그인한 유저만 글을 쓸 수 있으니 해당 메서드를 사용하는 게 맞다.
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException(" 댓글 쓰기 실패 : 로그인하지 않은 유저입니다. "));
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new IllegalArgumentException(" 댓글 쓰기 실패 : 존재하지 않는 게시물입니다."));

        Comment comment = Comment.builder()
                .content(commentRequestDto.getContent())
                .user(user)
                .post(post)
                .build();

        Comment savedComment = commentRepository.save(comment);

        return findComments(commentRequestDto, savedComment.getPost().getId());
    }


    // 댓글 수정
    // post/{post-id}/comments/{comment-id}
    public List<CommentResponseDto> update(CommentRequestDto commentRequestDto, Long commentId){
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new IllegalArgumentException("댓글 쓰기 실패 : 존재하지 않는 댓글입니다."));

        comment.update(commentRequestDto.getContent());
        Comment savedComment = commentRepository.save(comment);

        return findComments(commentRequestDto, savedComment.getPost().getId());
    }

    //댓글 삭제
    //  post/{post-id}/comments/{comment-id}
    public List<CommentResponseDto> Delete(CommentRequestDto commentRequestDto, Long commentId){
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new IllegalArgumentException("댓글 쓰기 실패 : 존재하지 않는 댓글입니다."));
        commentRepository.delete(comment);

        return findComments(commentRequestDto, comment.getPost().getId());
    }


    private PageRequest makePageRequest(CommentRequestDto commentRequestDto){
        return PageRequest
                .of(commentRequestDto.getPageNumber(), 10, Sort.by(Sort.Direction.ASC, "createDate"));
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
                    .createDate(commentEntity.getCreateDate())
                    .nickname(commentEntity.getUser().getNickName())
                    .build();

            responseDtoList.add(commentResponseDto);

        }

        return  responseDtoList;
    }

}
