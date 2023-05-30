package com.umbrella.controller;

import com.umbrella.dto.comment.ChildCommentRequestDto;
import com.umbrella.dto.comment.ChildCommentResponseDto;
import com.umbrella.service.Impl.ChildCommentServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/comments")
@RequiredArgsConstructor
public class ChildCommentController {

    private final ChildCommentServiceImpl childCommentService;

    // 대댓글 전체 조회
    @GetMapping("/{comment_id}/childComments")
    public ResponseEntity<?> findChildComments(@PathVariable Long comment_id){
        List<ChildCommentResponseDto> responseDtoList = childCommentService.findChildComments(comment_id);

        return ResponseEntity.ok().body(responseDtoList);
    }

    // 대댓글 생성
    @PostMapping("/{comment_id}/childComments")
    public ResponseEntity<?> createChildComment(@Valid ChildCommentRequestDto childCommentRequestDto,
                                                @PathVariable Long comment_id){

        List<ChildCommentResponseDto> responseDtoList = childCommentService
                .createChildComment(childCommentRequestDto, comment_id);

        return ResponseEntity.ok().body(responseDtoList);
    }

    // 대댓글 수정
    @PutMapping("/{comment_id}/childComments/{childComment_id}")
    public ResponseEntity<?> updateChildComment(@Valid ChildCommentRequestDto childCommentRequestDto,
                                                @PathVariable Long comment_id, @PathVariable Long childComment_id){

        List<ChildCommentResponseDto> responseDtoList = childCommentService
                .updateChildComment(childCommentRequestDto, comment_id, childComment_id);

        return ResponseEntity.ok().body(responseDtoList);
    }

    // 대댓글 삭제
    @DeleteMapping("/{comment_id}/childComments/{childComment_id}")
    public ResponseEntity<?> deleteChildComment(@PathVariable Long comment_id, @PathVariable Long childComment_id){

        List<ChildCommentResponseDto> responseDtoList = childCommentService
                .deleteChildComment(comment_id, childComment_id);

        return ResponseEntity.ok().body(responseDtoList);
    }

}
