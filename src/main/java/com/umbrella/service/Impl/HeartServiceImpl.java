package com.umbrella.service.Impl;

import com.umbrella.domain.Heart.PostHeart;
import com.umbrella.domain.Heart.PostHeartRepository;
import com.umbrella.domain.Post.Post;
import com.umbrella.domain.Post.PostRepository;
import com.umbrella.domain.User.User;
import com.umbrella.domain.User.UserRepository;
import com.umbrella.domain.exception.PostException;
import com.umbrella.domain.exception.PostExceptionType;
import com.umbrella.domain.exception.UserException;
import com.umbrella.domain.exception.UserExceptionType;
import com.umbrella.dto.Heart.HeartRequestDto;
import com.umbrella.security.utils.SecurityUtil;
import com.umbrella.service.HeartService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;

@Service
@Transactional
@RequiredArgsConstructor
public class HeartServiceImpl implements HeartService {

    private final UserRepository userRepository;
    private final PostRepository postRepository;
    private final PostHeartRepository postHeartRepository;
    private final SecurityUtil securityUtil;

    @Override
    public void insert(HeartRequestDto requestDto) {
        User user = searchUser();
        Post post = searchPost(requestDto.getPostId());
        validateInsertUser(user,post);
        PostHeart postHeart =
                PostHeart.builder()
                .user(user)
                .post(post)
                .build();
        post.addHeart();
        postHeartRepository.save(postHeart);

    }

    @Override
    public void delete(HeartRequestDto requestDto) {
        User user = searchUser();
        Post post = searchPost(requestDto.getPostId());
        validateDeleteUser(user,post);
        PostHeart postHeart = postHeartRepository.findByUserAndPost(user,post);
        postHeartRepository.delete(postHeart);
    }

    public void validateInsertUser(User user, Post post){
        PostHeart postHeart = postHeartRepository.findByUserAndPost(user,post);
        if(!Objects.equals(user.getId(), securityUtil.getLoginUserId())){
            throw new UserException(UserExceptionType.UN_AUTHORIZE_ERROR);
        }
        if(postHeart != null){
            throw new PostException(PostExceptionType.ALREADY_PUSH_ERROR);
        }
    }
    public void validateDeleteUser(User user, Post post){
        PostHeart postHeart = postHeartRepository.findByUserAndPost(user,post);
        if(!Objects.equals(user.getId(), securityUtil.getLoginUserId())){
            throw new UserException(UserExceptionType.UN_AUTHORIZE_ERROR);
        }
        if(postHeart == null){
            throw new PostException(PostExceptionType.ALREADY_PUSH_ERROR);
        }
    }


    public User searchUser(){
        return userRepository.findById(securityUtil.getLoginUserId()).orElseThrow(() -> new UserException(UserExceptionType.NOT_FOUND_ERROR));
    }
    public Post searchPost(Long id){
        return postRepository.findById(id).orElseThrow(()-> new PostException(PostExceptionType.NOT_FOUND_POST));
    }
}
