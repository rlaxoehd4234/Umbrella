package com.umbrella.project_umbrella.domain.Post;

import com.umbrella.project_umbrella.domain.Comment.Comment;
import com.umbrella.project_umbrella.domain.User.User;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Range;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor
public class Post {

    @Id
    @GeneratedValue
    @Column(nullable = true)
    private Long id;

    @NotNull
    @Column(length = 500)
    private String title;

    @NotNull
    @Size(min = 50, max= 1000)
    @Column(columnDefinition = "TEXT")
    private String content;

    private String writer;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;


    public Post(Long id, String title) {
        this.id = id;
        this.title = title;
    }


    @Builder
    public Post(String writer, String title, String content){
        this.writer = writer;
        this.content = content;
        this.title = title;
    }


    public void update(String title, String content){
        this.title = title;
        this.content = content;
    }
}