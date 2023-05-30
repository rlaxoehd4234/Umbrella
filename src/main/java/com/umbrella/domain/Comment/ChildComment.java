package com.umbrella.domain.Comment;

import com.umbrella.domain.User.User;
import com.umbrella.dto.comment.ChildCommentRequestDto;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;

import javax.persistence.*;

@Entity
@Getter
@NoArgsConstructor
public class ChildComment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "child_comment_id")
    Long id;

    @OnDelete(action = OnDeleteAction.CASCADE)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "comment_id")
    Comment parentComment;

    @OnDelete(action = OnDeleteAction.CASCADE)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;


    @Column(columnDefinition = "TEXT" , nullable = false)
    private String content; // 변수명 변경


    @Column
    @CreatedDate
    private String createDate;

    @Column
    @LastModifiedDate
    private String modifiedDate;


    public void update(String content){
        this.content = content;
    }


    @Builder
    public ChildComment(String content, Comment comment, User user){
        this.content = content;
        this.parentComment = comment;
        this.user = user;
    }



}
