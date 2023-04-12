package com.umbrella.service.Impl;


import com.umbrella.domain.Comment.CommentRepository;
import com.umbrella.domain.Post.Post;
import com.umbrella.domain.Post.PostRepository;
import com.umbrella.domain.User.User;
import com.umbrella.domain.User.UserRepository;
import com.umbrella.dto.exception.PostException;
import com.umbrella.dto.exception.PostExceptionType;
import com.umbrella.dto.post.*;
import com.umbrella.security.utils.SecurityUtil;
import com.umbrella.service.PostService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;
import java.util.Optional;


@Service
@RequiredArgsConstructor
@Transactional
public class PostServiceImpl implements PostService {
    private final PostRepository postRepository;
    private final UserRepository userRepository;
    private final CommentRepository commentRepository;
    private final SecurityUtil securityUtil;




    // 저장 메서드
    public Long save(PostSaveRequestDto requestDto) throws IllegalAccessException {
        Long Id = securityUtil.getLoginUserId();
        Optional<User> findUser = Optional.ofNullable(userRepository.findById(Id).orElseThrow(() -> new IllegalArgumentException("존재하지 않는유저 입니다.")));

        validateUser(findUser);

        Post post = Post.builder()
                .content(requestDto.getContent())
                .writer(findUser.orElseThrow().getNickName())
                .title(requestDto.getTitle())
                .user(findUser.orElseThrow())
                .build();

        return postRepository.save(post).getId();
    }


    // 수정 메서드
    public Long update(Long id, PostUpdateRequestDto requestDto){
        Optional<User> user = userRepository.findById(securityUtil.getLoginUserId());
        validateUser(user);
        Post post = validatePost(id);
        post.update(requestDto.getTitle(), requestDto.getContent());

        return id;
    }

    // 삭제 메서드
    public Long delete(Long id){
        // 요청하는 유저의 아이디가 일치하는지에 대한 검증 로직 추가
        Post post = validatePost(id);
        postRepository.delete(post);

        return id;
    }

    // 게시글 클릭 메서드
    public PostResponseDto findById(Long id){
        Post post = validatePost(id);
        return new PostResponseDto(post);

    }

    // 게시글 전체 리턴 메서드
    @Transactional(readOnly = true)
    public Page<PostListResponseDto> findAllPosts(Pageable pageable){

        Page<Post> page = postRepository.findAll(pageable);
        Page<PostListResponseDto> map = page.map(PostListResponseDto::new);

        return map;
    }


    public Page<PostListResponseDto> findSearchPost(String title, Pageable pageable) {

        Page<Post> page = postRepository.findByTitleContaining(title, pageable);
        Page<PostListResponseDto> map = page.map(PostListResponseDto::new);

        return map;
    }

    public void validateUser(Optional<User> user)  {
        if(!Objects.equals(securityUtil.getLoginUserId(), user.get().getId()){
            return new

        }
    }

    public Post validatePost(Long id){
        return postRepository.findById(id)
                .orElseThrow(() -> new PostException(PostExceptionType.NOT_FOUND_POST));
    }



}
