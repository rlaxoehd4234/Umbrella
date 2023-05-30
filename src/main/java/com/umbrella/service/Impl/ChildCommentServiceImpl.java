package com.umbrella.service.Impl;

import com.umbrella.domain.Comment.ChildComment;
import com.umbrella.domain.Comment.ChildCommentRepository;
import com.umbrella.domain.Comment.Comment;
import com.umbrella.domain.Comment.CommentRepository;
import com.umbrella.domain.User.User;
import com.umbrella.domain.User.UserRepository;
import com.umbrella.domain.exception.CommentException;
import com.umbrella.domain.exception.CommentExceptionType;
import com.umbrella.domain.exception.UserException;
import com.umbrella.domain.exception.UserExceptionType;
import com.umbrella.dto.comment.ChildCommentRequestDto;
import com.umbrella.dto.comment.ChildCommentResponseDto;
import com.umbrella.security.utils.SecurityUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
@Transactional
public class ChildCommentServiceImpl {

    private final ChildCommentRepository childCommentRepository;
    private final CommentRepository commentRepository;
    private final UserRepository userRepository;
    private final SecurityUtil securityUtil;

    // 대댓글 전체 조회 : /comments/{comment_id}/childComments
    public List<ChildCommentResponseDto> findChildComments(Long parentCommentId){

        List<ChildComment> childCommentList = childCommentRepository
                .findAllByParentComment_Id(parentCommentId, Sort.by("id"));

        List<ChildCommentResponseDto> responseDtoList = new ArrayList<>();

        for (ChildComment childComment : childCommentList) {

            ChildCommentResponseDto childCommentResponseDto = ChildCommentResponseDto
                    .builder()
                    .nickName(childComment.getUser().getNickName())
                    .content(childComment.getContent())
                    .build();

            responseDtoList.add(childCommentResponseDto);

        }
        return responseDtoList;
    }

    // 대댓글 생성 : /comments/{comment_id}/childComments
    public List<ChildCommentResponseDto> createChildComment(ChildCommentRequestDto childCommentRequestDto, Long parentCommentId){

        User user = userRepository.findById(securityUtil.getLoginUserId())
                .orElseThrow(() -> new UserException(UserExceptionType.NOT_FOUND_ERROR));

        Comment comment = commentRepository.findById(parentCommentId)
                .orElseThrow(() -> new CommentException(CommentExceptionType.NOT_FOUND_COMMENT));

        ChildComment childComment = ChildComment.builder()
                .content(childCommentRequestDto.getContent())
                .comment(comment)
                .user(user)
                .build();

        ChildComment savedChildComment = childCommentRepository.save(childComment);

        return findChildComments(savedChildComment.getParentComment().getId());
    }

    // 대댓글 수정 : /comments/{comment_id}/childComments/{childComment-id}
    public List<ChildCommentResponseDto> updateChildComment(ChildCommentRequestDto childCommentRequestDto,
                                                            Long parentCommentId, Long childCommentId){

        ChildComment childComment = validateChildComment(childCommentId);
        validateUser(childComment.getUser()); // 수정할 수 있는 유저인지 검증

        childComment.update(childCommentRequestDto.getContent());

        return findChildComments(parentCommentId);
    }



    // 대댓글 삭제 : /comments/{comment_id}/childComments/{childComment-id}
    public List<ChildCommentResponseDto> deleteChildComment(Long parentCommentId, Long childCommentId){

        ChildComment childComment = validateChildComment(childCommentId);
        validateUser(childComment.getUser()); // 삭제할 수 있는 유저인지 검증

        childCommentRepository.delete(childComment);

        return findChildComments(parentCommentId);
    }




    // 변경 권한, 삭제 권한을 소유한 유저인지
    private void validateUser(User user){
        if(!Objects.equals(user.getId(), securityUtil.getLoginUserId())){
            throw new UserException(UserExceptionType.UN_AUTHORIZE_ERROR);
        }
    }

    private ChildComment validateChildComment(Long childCommentId){

        return childCommentRepository.findById(childCommentId)
                .orElseThrow(() -> new CommentException(CommentExceptionType.NOT_FOUND_CHILD_COMMENT));
    }

}
