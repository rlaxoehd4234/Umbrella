package com.umbrella.domain.Heart;

import com.umbrella.domain.Post.Post;
import com.umbrella.domain.User.User;
import org.springframework.data.jpa.repository.JpaRepository;


public interface PostHeartRepository extends JpaRepository<PostHeart,Long> {
    PostHeart findByUserAndPost(User user, Post post);

}
