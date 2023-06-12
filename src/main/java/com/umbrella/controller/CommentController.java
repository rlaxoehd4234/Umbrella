package com.umbrella.controller;

import com.umbrella.dto.comment.CommentPageRequestDto;
import com.umbrella.dto.comment.CommentRequestDto;
import com.umbrella.dto.comment.CommentResponseDto;
import com.umbrella.service.Impl.CommentServiceImpl;
import lombok.RequiredArgsConstructor;
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
    public ResponseEntity<?> findComments(@PathVariable Long post_id, @Valid @RequestBody CommentPageRequestDto dto){
        List<CommentResponseDto> response = commentService.findComments(dto.getPageNumber(), post_id);
        return ResponseEntity.ok().body(response); // 200 OK
    }

    // 댓글 생성
    @PostMapping("/{post_id}/comments")
    public ResponseEntity<?> createComment(@PathVariable Long post_id, @Valid @RequestBody CommentRequestDto dto){
        List<CommentResponseDto> response = commentService.create(dto, post_id);
        return ResponseEntity.ok().body(response); // 200 OK
    }

    // 댓글 수정
    @PutMapping("/{post_id}/comments/{comment_id}")
    public ResponseEntity<?> updateComment(@PathVariable Long comment_id, @PathVariable Long post_id,
                                            @Valid @RequestBody CommentRequestDto dto){
        List<CommentResponseDto> response = commentService.update(dto, post_id, comment_id);
        return ResponseEntity.ok().body(response); // 200 OK
    }

    // 댓글 삭제
    @DeleteMapping("/{post_id}/comments/{comment_id}")
    public ResponseEntity<?> deleteComment(@PathVariable Long post_id, @PathVariable Long comment_id,
                                           @Valid @RequestBody CommentRequestDto dto){
        List<CommentResponseDto> response = commentService.delete(dto, post_id, comment_id);
        return ResponseEntity.ok().body(response); // 200 OK
    }




}
