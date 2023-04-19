package com.umbrella.domain.Chat;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ChatMessageRepository extends JpaRepository<ChatMessage, Long> {

    List<ChatMessage> findByChatRoom_ChatRoomName(String chatRoomName);

    @Modifying
    @Query("DELETE FROM ChatMessage c WHERE c.chatRoom.chatRoomName = :chatRoomName")
    void deleteAllByChatRoom_ChatRoomName(@Param("chatRoomName") String chatRoomName);
    // 채팅방 삭제의 경우 채팅 기록 전부 삭제
}
