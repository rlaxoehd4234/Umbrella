package com.umbrella.controller;

import com.umbrella.dto.comment.CommentDeleteDto;
import com.umbrella.dto.comment.CommentRequestDto;
import com.umbrella.dto.comment.CommentResponseDto;
import com.umbrella.dto.comment.CommentUpdateDto;
import com.umbrella.service.Impl.CommentServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/posts")
@RequiredArgsConstructor
public class CommentController {


    private final CommentServiceImpl commentService;

    // 댓글 전체 조회
    @GetMapping("/{post_id}/comments")
    public ResponseEntity<?> findComments(@PathVariable Long post_id, @PageableDefault(size = 10) Pageable pageable){
        return ResponseEntity.ok().body(commentService.findAllComments(post_id, pageable));
    }

    // 댓글 생성
    @PostMapping("/{post_id}/comments/create")
    public ResponseEntity<?> createComment(@PathVariable Long post_id, @Valid @RequestBody CommentRequestDto dto,
                                                                    @PageableDefault(size = 10) Pageable pageable){
        return ResponseEntity.ok().body(commentService.createComment(post_id, dto, pageable));
    }

    // 댓글 수정
    @PutMapping("/{post_id}/comments/update")
    public ResponseEntity<?> updateComment(@PathVariable Long post_id,
                                           @PageableDefault(size = 10) Pageable pageable, @Valid @RequestBody CommentUpdateDto dto){

        return ResponseEntity.ok().body(commentService.updateComment(post_id, dto, pageable));
    }

    // 댓글 삭제
    @DeleteMapping("/{post_id}/comments/delete")
    public ResponseEntity<?> deleteComment(@PathVariable Long post_id, @PageableDefault(size = 10) Pageable pageable,
                                           @Valid @RequestBody CommentDeleteDto commentDeleteDto){
       return ResponseEntity.ok().body(commentService.deleteComment(post_id, commentDeleteDto, pageable));
    }




}
