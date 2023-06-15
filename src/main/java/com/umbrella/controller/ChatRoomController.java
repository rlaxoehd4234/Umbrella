package com.umbrella.controller;


import com.umbrella.dto.chatRoom.CreateChatRoomDto;
import com.umbrella.dto.chatRoom.JoinChatRoomDto;
import com.umbrella.dto.chatRoom.UpdateChatRoomNameDto;
import com.umbrella.service.Impl.ChatRoomService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;


@RestController
@RequiredArgsConstructor
public class ChatRoomController {

    private final ChatRoomService chatRoomService;

    // /workspace/{workspace_id}/chatRooms
    // 워크스페이스 안에 있는 모든 채팅방
    @GetMapping("/workspace/{workspace_id}/chatRooms")
    public ResponseEntity<?> allChatRoomsInWorkspace(@PathVariable Long workspace_id){

        return ResponseEntity.ok().body(chatRoomService.allChatRoomsInWorkSpace(workspace_id));
    }

    // /workspace/userChatRooms
    @GetMapping("/workspace/userChatRooms")
    public ResponseEntity<?> allChatRoomsInWorkspace(){
        return ResponseEntity.ok().body(chatRoomService.allChatRoomsInUser());
    }

    @PostMapping("/workspace/{workspace_id}/createChatRoom")
    public ResponseEntity<?> createChatRoom(@PathVariable Long workspace_id, @Valid @RequestBody CreateChatRoomDto createChatRoomDto){
        return ResponseEntity.ok().body(chatRoomService.saveChatRoom(workspace_id, createChatRoomDto));
    }

    @PostMapping("/workspace/joinChatRoom")
    public ResponseEntity<?> joinChatRoom(@Valid @RequestBody JoinChatRoomDto joinChatRoomDto){
        chatRoomService.joinChatRoom(joinChatRoomDto);
        return ResponseEntity.ok().body("유저가 채팅방에 참여하였습니다.");
    }

    @PutMapping("/workspace/{workspace_id}/updateChatRoom")
    public ResponseEntity<?> updateChatRoomName(@PathVariable Long workspace_id, @Valid @RequestBody UpdateChatRoomNameDto updateChatRoomNameDto){
        return ResponseEntity.ok().body(chatRoomService.updateChatRoomName(workspace_id, updateChatRoomNameDto));
    }

    @DeleteMapping("/workspace/{workspace_id}/deleteChatRoom/{chatRoom_id}")
    public ResponseEntity<?> deleteChatRoom(@PathVariable Long workspace_id, @PathVariable Long chatRoom_id){
        return ResponseEntity.ok().body(chatRoomService.deleteChatRoom(workspace_id, chatRoom_id));
    }


}

