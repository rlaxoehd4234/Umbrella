package com.umbrella.service.Impl;

import com.umbrella.domain.Chat.*;
import com.umbrella.domain.User.User;
import com.umbrella.domain.User.UserRepository;
import com.umbrella.domain.exception.UserException;
import com.umbrella.domain.exception.UserExceptionType;
import com.umbrella.dto.chat.ChatMessageLogDto;
import com.umbrella.dto.chat.ChatRoomsResponseDto;
import com.umbrella.security.utils.SecurityUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class ChatRoomServiceImpl {

    private final ChatRoomJoinRepository chatRoomJoinRepository;
    private final ChatRoomRepository chatRoomRepository;
    private final UserRepository userRepository;
    private final ChatMessageRepository chatMessageRepository;
    private final SecurityUtil securityUtil;

    //채팅방 이름 전체 조회 -> 페이징 적용(나중)
    public ChatRoomsResponseDto<?> findChatRoomNameList(){
         List<ChatRoom> chatRoomList = chatRoomRepository.findAll();
         List<String> chatRoomNameList = new ArrayList<>();

        for (ChatRoom chatRoom : chatRoomList) {
            chatRoomNameList.add(chatRoom.getChatRoomName());
        }


        return ChatRoomsResponseDto.builder()
                .chatRoomNameList(chatRoomNameList)
                .build();
    }
    
    // roomName 으로 채팅방 생성
    // 로그인하지 않은 유저는 채팅방을 생성할 수 없습니다.
    public void createChatRoom(String roomName){

        userRepository.findById(securityUtil.getLoginUserId())
                .orElseThrow(() -> new UserException(UserExceptionType.UN_AUTHORIZE_ERROR));
        // 로그인하지 않은 유저는 채팅방을 생성할 수 없습니다.

        ChatRoom chatRoom = ChatRoom.builder()
                .chatRoomName(roomName)
                .build();
        chatRoomRepository.save(chatRoom);
    }

    public ChatMessageLogDto<?> joinChatRoom(String roomName){
        User user = userRepository.findById(securityUtil.getLoginUserId())
                .orElseThrow(() -> new UserException(UserExceptionType.UN_AUTHORIZE_ERROR)); // 로그인하지 않은 유저는 채팅방을 입장할 수 없습니다.

        ChatRoom chatRoom = chatRoomRepository.findByChatRoomName(roomName);
        chatRoom.addUserCount(); // dirtyCheck

        ChatRoomJoin chatRoomJoin = ChatRoomJoin.builder()
                .user(user)
                .chatRoom(chatRoom)
                .build();
        chatRoomJoinRepository.save(chatRoomJoin); //유저 채팅방 조인(합류)

        List<ChatMessage> messages = chatMessageRepository.findByChatRoom_ChatRoomName(chatRoom.getChatRoomName());

        return ChatMessageLogDto.builder()
                .chatMessageLog(messages)
                .build();
    }

    // 채팅방 유저 입장 -> 연관관계 저장, 채팅방 인원 +1
    // ->채팅에 참여하고 있는 유저들 리스트, 그간 채팅했던 로그들,  ~님이 입장하셨습니다.
    // 채팅방 인원 +1
    // 채팅방 인원 -1

    // 현재 채팅에 참여하고 있는 유저 리스트
    // 채팅방 유저 리스트에 유저 추가


    // 채팅방 삭제 -> 채팅 로그, 채팅 방 삭제 -> 채팅방 이름으로 삭제
}
