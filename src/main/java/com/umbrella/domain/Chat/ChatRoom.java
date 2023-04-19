package com.umbrella.domain.Chat;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.ColumnDefault;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ChatRoom {

    @Id @GeneratedValue
    @Column(name = "chat_romm_id")
    private Long chatRoomId;

    @Column(columnDefinition = "TEXT" , nullable = false)
    private String chatRoomName;

    @ColumnDefault("0")// default 0
    private Long userCount; // 채팅방 인원 수

    @Builder
    public ChatRoom(String chatRoomName) {
        this.chatRoomName = chatRoomName;
    }

    public void addUserCount(){
        userCount++;
    }

    public void popUserCount(){
        userCount--;
    }

}
