package com.umbrella.domain.Chat;

import com.umbrella.domain.User.User;
import jdk.jfr.Timestamp;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;

import javax.persistence.*;

import static javax.persistence.FetchType.LAZY;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ChatMessage {
    // 어떤 유저가 어떤 채팅방에 어떤 메세지를 나타내기 위한 테이블
    // 이미지 파일 형식은 추후 업데이트
    @Id @GeneratedValue
    private Long messageId;

    @Column(columnDefinition = "TEXT" , nullable = false)
    String message;

    @Column
    @CreatedDate
    private String createdTime;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "room_id")
    private ChatRoom chatRoom;


}
