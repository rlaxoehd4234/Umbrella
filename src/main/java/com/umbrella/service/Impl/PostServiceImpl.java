package com.umbrella.service.Impl;


import com.umbrella.domain.Board.Board;
import com.umbrella.domain.Board.BoardRepository;
import com.umbrella.domain.Comment.CommentRepository;
import com.umbrella.domain.Heart.PostHeartRepository;
import com.umbrella.domain.Post.Post;
import com.umbrella.domain.Post.PostRepository;
import com.umbrella.domain.User.User;
import com.umbrella.domain.User.UserRepository;
import com.umbrella.domain.exception.*;
import com.umbrella.dto.post.*;
import com.umbrella.security.utils.SecurityUtil;
import com.umbrella.service.PostService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;


@Service
@RequiredArgsConstructor
@Transactional
public class PostServiceImpl implements PostService {
    private final PostRepository postRepository;
    private final UserRepository userRepository;
    private final BoardRepository boardRepository;
    private final PostHeartRepository postHeartRepository;
    private final SecurityUtil securityUtil;

    // postImgService add
    private final PostImgServiceImpl postImgService;


    // 저장 메서드
    public Long save(Long board_id, PostSaveRequestDto requestDto){
        User findUser = userRepository.findById(securityUtil.getLoginUserId()).orElseThrow(() -> new UserException(UserExceptionType.NOT_FOUND_ERROR));
        Board board = validateBoard(board_id);
        validateUser(findUser);

        Post post = Post.builder()
                .content(requestDto.getContent())
                .writer(findUser.getName())
                .title(requestDto.getTitle())
                .user(findUser)
                .board(board)
                .likeCount(0)
                .build();

        Post savedPost = postRepository.save(post);

        postImgService.createPostImg(requestDto, post);

        return savedPost.getId();
    }


    // 수정 메서드
    public Long update(Long board_id, Long post_id, PostUpdateRequestDto requestDto){
        validateBoard(board_id);
        Post post = validatePost(post_id);
        validateUser(post.getUser());
        post.update(requestDto.getTitle(), requestDto.getContent());

        postImgService.updatePostImg(post, requestDto);

        return post_id;
    }

    // 삭제 메서드
    public Long delete(Long board_id, Long post_id){
        Post post = validatePost(post_id);
        validateBoard(board_id);
        validateUser(post.getUser());
        postRepository.delete(post);
        postHeartRepository.delete(postHeartRepository.findByUserAndPost(post.getUser(),post));
        postImgService.postImgDeletedByPostId(post_id);
        return post_id;
    }

    // 게시글 클릭 메서드
    public PostResponseDto findById(Long board_id, Long post_id) {
        validateBoard(board_id);
        Post post = validatePost(post_id);
        validateUser(post.getUser());
        return new PostResponseDto(post);
    }

    // 게시글 전체 리턴 메서드
    @Transactional(readOnly = true)
    public Page<PostListResponseDto> findAllPosts(Long board_id, Pageable pageable) {
        // TODO: User 타당성 검증
        Page<Post> page = postRepository.findAllByBoardId(board_id,pageable);

        return page.map(PostListResponseDto::new);
    }

    @Transactional(readOnly = true)
    public Page<PostListResponseDto> findSearchPost(String title, Pageable pageable) {
        // TODO: User 타당성 검증
        Page<Post> page = postRepository.findByTitleContaining(title, pageable);

        return page.map(PostListResponseDto::new);
    }

    public void validateUser(User user)  {
        if(!Objects.equals(user.getId(), securityUtil.getLoginUserId())){
            throw new UserException(UserExceptionType.UN_AUTHORIZE_ERROR);
        }
    }

    public Post validatePost(Long id){
        return postRepository.findById(id)
                .orElseThrow(() -> new PostException(PostExceptionType.NOT_FOUND_POST));
    }

    public Board validateBoard(Long board_id){
        return boardRepository.findById(board_id).orElseThrow(() -> new BoardException(BoardExceptionType.NOT_FOUND_BOARD));
    }



}
