package com.umbrella.domain.ChatRoom;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ChatRoomRepository extends JpaRepository<ChatRoom, Long> {
    List<ChatRoom> findAllByWorkSpaceId(Long workSpaceId);



}
