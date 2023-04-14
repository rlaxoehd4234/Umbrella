package com.umbrella.domain.Chat;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ChatMessageRepository extends JpaRepository<ChatMessage, Long> {

    List<ChatMessage> findByChatRoom_ChatRoomName(String chatRoomName);

    @Query
    void deleteAllByChatRoom_ChatRoomName(String chatRoom); // 채팅방 삭제의 경우
}
