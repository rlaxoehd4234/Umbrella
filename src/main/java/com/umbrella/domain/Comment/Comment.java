package com.umbrella.domain.Comment;

import com.umbrella.domain.Post.Post;
import com.umbrella.domain.User.User;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor
public class Comment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "comment_id")
    private Long id;

    @Column(columnDefinition = "TEXT" , nullable = false)
    private String content; // 변수명 변경

    @Column
    @CreatedDate
    private String createDate;

    @Column
    @LastModifiedDate
    private String modifiedDate;

    @ManyToOne
    @JoinColumn(name = "posts_id")
    private Post post;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;


    @OneToMany(mappedBy = "comment", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("id asc")
    private List<ChildComment> childCommentList = new ArrayList<>();

    @Builder
    public Comment(Long id, String content, String createDate, String modifiedDate, Post post, User user) {
        this.id = id;
        this.content = content;
        this.createDate = createDate;
        this.modifiedDate = modifiedDate;
        this.post = post;
        this.user = user;
    }

    // 댓글 수정을 위한 setter
    public void update(String content){
        this.content = content;
    }





}
