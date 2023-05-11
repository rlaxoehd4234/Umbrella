package com.umbrella.domain.Comment;

import lombok.Getter;
import lombok.NoArgsConstructor;
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

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "comment_id")
    Comment comment;

    @Column(columnDefinition = "TEXT" , nullable = false)
    private String content; // 변수명 변경

    @Column
    @CreatedDate
    private String createDate;

    @Column
    @LastModifiedDate
    private String modifiedDate;

    public ChildComment(String content, Comment comment, ChildComment childComment){
        this.content = content;
        setChildComment(comment, childComment);
    }

    // 연관관계 편의 메소드
    public void setChildComment(Comment comment, ChildComment childComment){
        this.comment = comment;
        comment.getChildCommentList().add(childComment);
    }




}
