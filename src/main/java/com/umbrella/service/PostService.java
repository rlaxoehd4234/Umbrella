package com.umbrella.project_umbrella.service;


import com.umbrella.project_umbrella.domain.Comment.CommentRepository;
import com.umbrella.project_umbrella.domain.Post.Post;
import com.umbrella.project_umbrella.domain.Post.PostRepository;
import com.umbrella.project_umbrella.domain.User.User;
import com.umbrella.project_umbrella.domain.User.UserRepository;
import com.umbrella.project_umbrella.dto.post.PostListResponseDto;
import com.umbrella.project_umbrella.dto.post.PostResponseDto;
import com.umbrella.project_umbrella.dto.post.PostSaveRequestDto;
import com.umbrella.project_umbrella.dto.post.PostUpdateRequestDto;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;



@Service
@RequiredArgsConstructor
@Transactional
public class PostService {
    private final PostRepository postRepository;
    private final UserRepository userRepository;
    private final CommentRepository commentRepository;


    // 저장 메서드
    public Long save(PostSaveRequestDto requestDto){


        User findedUser = requestDto.toEntity().getUser();

        Post post = Post.builder()
                .content(requestDto.getContent())
                .writer(findedUser.getNickName())
                .title(requestDto.getTitle())
                .build();

        return postRepository.save(post).getId();
    }


    // 수정 메서드
    public Long update(Long id, PostUpdateRequestDto requestDto){

        Post post = postRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("해당 게시글이 없습니다."));

        post.update(requestDto.getTitle(), requestDto.getContent());

        return id;
    }

    // 삭제 메서드
    public Long delete(Long id){

        Post post = postRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("해당 게시글이 없습니다."));

        postRepository.delete(post);

        return id;
    }

    // 게시글 클릭 메서드
    public PostResponseDto findById(Long id){

        Post post = postRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("게시글이 존재하지 않습니다."));

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
}
