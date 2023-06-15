package com.umbrella.service.Impl;

import com.umbrella.domain.ChatRoom.ChatRoom;
import com.umbrella.domain.ChatRoom.ChatRoomRepository;
import com.umbrella.domain.ChatRoom.ChatRoomUserJoin;
import com.umbrella.domain.ChatRoom.ChatRoomUserJoinRepository;
import com.umbrella.domain.User.User;
import com.umbrella.domain.User.UserRepository;
import com.umbrella.domain.WorkSpace.WorkSpace;
import com.umbrella.domain.WorkSpace.WorkSpaceRepository;
import com.umbrella.domain.exception.*;
import com.umbrella.dto.chatRoom.ChatRoomDto;
import com.umbrella.dto.chatRoom.CreateChatRoomDto;
import com.umbrella.dto.chatRoom.JoinChatRoomDto;
import com.umbrella.dto.chatRoom.UpdateChatRoomNameDto;
import com.umbrella.security.utils.SecurityUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;


import static com.umbrella.domain.exception.ChatRoomExceptionType.*;

@Service
@RequiredArgsConstructor
@Transactional
public class ChatRoomService {

    private final ChatRoomRepository chatRoomRepository;
    private final ChatRoomUserJoinRepository chatRoomUserJoinRepository;
    private final WorkSpaceRepository workSpaceRepository;
    private final UserRepository userRepository;
    private final SecurityUtil securityUtil;

    // 해당 워크스페이스에서 유저가 속한 모든 채팅방을 보여준다, 채팅방에 어떤 인원들이 있는지 보여준다.
    public List<ChatRoomDto> allChatRoomsInUser(){
        // 로그인 한 유저
        User user = userRepository.findById(securityUtil.getLoginUserId())
                .orElseThrow(() -> new UserException(UserExceptionType.NOT_FOUND_ERROR));

        // 유저가 속한 모든 채팅방 갖고오기
        List<ChatRoomUserJoin> chatRoomUserJoins = chatRoomUserJoinRepository
                .findAllByUserId(user.getId());

        List<ChatRoomDto> chatRoomDtoList = new ArrayList<>();

        for (ChatRoomUserJoin chatRoomUserJoin : chatRoomUserJoins) {
            ChatRoomDto chatRoomDto = ChatRoomDto.builder()
                    .chatRoomId(chatRoomUserJoin.getChatRoom().getId())
                    .roomName(chatRoomUserJoin.getChatRoom().getRoomName())
                    .createdBy(chatRoomUserJoin.getChatRoom().getCreatedBy())
                    .build();

            chatRoomDtoList.add(chatRoomDto);
        }

        return chatRoomDtoList;
    }


    // 워크 스페이스에 있는 채팅방 전부 보여주기, 채팅방에 어떤 인원들이 있는지 보여준다.
    public List<ChatRoomDto> allChatRoomsInWorkSpace(Long workSpaceId){

        List<ChatRoom> chatRoomList = chatRoomRepository.findAllByWorkSpaceId(workSpaceId);
        
        List<ChatRoomDto> chatRoomDtoList = new ArrayList<>();

        for (ChatRoom chatRoom : chatRoomList) {
            ChatRoomDto chatRoomDto = ChatRoomDto.builder()
                    .chatRoomId(chatRoom.getId())
                    .roomName(chatRoom.getRoomName())
                    .createdBy(chatRoom.getCreatedBy())
                    .build();

            chatRoomDtoList.add(chatRoomDto);
        }
        return chatRoomDtoList;
    }

    // 채팅방 생성, 작성자는 자동 채팅방 참여
    public List<ChatRoomDto> saveChatRoom(Long workspaceId, CreateChatRoomDto createChatRoomDto){

        // 채팅방 이름 중복 체크
        String chatRoomName = validateChatRoomName(createChatRoomDto.getRoomName());

        User user = userRepository.findById(securityUtil.getLoginUserId())
                .orElseThrow(() -> new UserException(UserExceptionType.NOT_FOUND_ERROR));

        ChatRoom chatRoom = ChatRoom.builder()
                .workSpace(validateWorkspace(workspaceId))
                .roomName(chatRoomName)
                .createdBy(user.getNickName())
                .build();

        ChatRoom savedChatRoom = chatRoomRepository.save(chatRoom);

        ChatRoomUserJoin chatRoomUserJoin = ChatRoomUserJoin.builder()
                .chatRoom(savedChatRoom)
                .user(user)
                .build(); // 채팅방 작성자는 바로 채팅방 생성시 채팅방에 들어가겠끔

        chatRoomUserJoinRepository.save(chatRoomUserJoin);

        return allChatRoomsInWorkSpace(workspaceId);
    }

    // 채팅방 참여 -> 어떤 유저가 어떤 채팅방에 참여했는지
    public void joinChatRoom(JoinChatRoomDto joinChatRoomDto){

        User user = userRepository.findById(securityUtil.getLoginUserId())
                .orElseThrow(() -> new UserException(UserExceptionType.NOT_FOUND_ERROR));

        ChatRoom chatRoom = chatRoomRepository.findById(joinChatRoomDto.getChatRoomId())
                .orElseThrow(() -> new ChatRoomException(NOT_FOUND_CHATROOM));

        ChatRoomUserJoin chatRoomUserJoin = ChatRoomUserJoin.builder()
                .user(user)
                .chatRoom(chatRoom)
                .build();

        chatRoomUserJoinRepository.save(chatRoomUserJoin);

    }

    // 채팅방 이름 업데이트 -> 작성자만 가능
    public List<ChatRoomDto> updateChatRoomName(Long workSpaceId, UpdateChatRoomNameDto updateChatRoomNameDto){
        ChatRoom chatRoom = validateChatRoom(updateChatRoomNameDto.getChatRoomId());

        chatRoom.chatRoomNameUpdate(updateChatRoomNameDto.getUpdateChatRoomName());

        chatRoomRepository.save(chatRoom);
        return allChatRoomsInWorkSpace(workSpaceId);
    }

    // 채팅방 삭제 -> 작성자만 가능, MongoDB에도 챗 기록 삭제 DTO 날려야 함
    public List<ChatRoomDto> deleteChatRoom(Long workSpaceId, Long chatRoomId){
        ChatRoom chatRoom = validateChatRoom(chatRoomId);

        chatRoomRepository.delete(chatRoom);

        return allChatRoomsInWorkSpace(workSpaceId);
    }


    private WorkSpace validateWorkspace(Long id){
        return workSpaceRepository.findById(id)
                .orElseThrow(() -> new WorkspaceException(WorkspaceExceptionType.NOT_FOUND_WORKSPACE));
    }
    
    // 채팅방 이름 중복 체크
    private String validateChatRoomName(String chatRoomName){

        List<ChatRoom> allChatRooms = chatRoomRepository.findAll();

        for (ChatRoom chatRoom : allChatRooms) {
            if(chatRoom.getRoomName().equals(chatRoomName)){
                throw new ChatRoomException(DUPLICATED_CHATROOM_NAME);
            }
        }

        return chatRoomName;
    }

    // 채팅방 생성자 권한 체크
    private ChatRoom validateChatRoom(Long chatRoomId){
        ChatRoom chatRoom = chatRoomRepository.findById(chatRoomId)
                .orElseThrow(() -> new ChatRoomException(NOT_FOUND_CHATROOM));

        // 로그인 한 유저
        User user = userRepository.findById(securityUtil.getLoginUserId())
                .orElseThrow(() -> new UserException(UserExceptionType.NOT_FOUND_ERROR));

        if(!chatRoom.getCreatedBy().equals(user.getNickName())){
            throw new ChatRoomException(NOT_OWNER);
        }

        return chatRoom;
    }


}
