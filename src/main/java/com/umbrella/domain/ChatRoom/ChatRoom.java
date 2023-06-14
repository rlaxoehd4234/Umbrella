package com.umbrella.domain.ChatRoom;


import com.umbrella.domain.WorkSpace.WorkSpace;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;

import javax.persistence.*;


@NoArgsConstructor
@Getter
@Entity
public class ChatRoom {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "chatRoom_id")
    private Long id;

    @OnDelete(action = OnDeleteAction.CASCADE)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "workspace_id")
    WorkSpace workSpace;
    // FK : 어떤 워크스페이스에 있는 채팅방인지

    @Column(nullable = false, unique = true)
    private String roomName;

    @Column(nullable = false)
    private String createdBy; // 채팅방 작성자

    @Column
    @CreatedDate
    private String createDate;

    @Column
    @LastModifiedDate
    private String modifiedDate;

    @Builder
    public ChatRoom(WorkSpace workSpace, String roomName, String createdBy) {
        this.createdBy = createdBy;
        this.workSpace = workSpace;
        this.roomName = roomName;
    }

    public void chatRoomNameUpdate(String updateName){
        this.roomName = updateName;
    }


}
