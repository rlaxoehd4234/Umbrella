package com.umbrella.domain.ChatRoom;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface ChatRoomUserJoinRepository extends JpaRepository<ChatRoomUserJoin, Long> {

    List<ChatRoomUserJoin> findAllByUserId(Long userId);

}
