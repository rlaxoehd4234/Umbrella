package com.umbrella.domain.Chat;

import org.springframework.data.jpa.repository.JpaRepository;

public interface ChatRoomRepository extends JpaRepository<ChatRoom, Long> {

    ChatRoom findByChatRoomName(String chatRoomName);
}
