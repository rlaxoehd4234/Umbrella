package com.umbrella.dto.user;

import com.umbrella.constant.Role;
import com.umbrella.domain.User.User;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class UserInWorkspaceDTO {

    private Long userId;

    private String email;

    private String nickName;

    private Role userRole;

    @Builder
    public UserInWorkspaceDTO(User user) {
        this.userId = user.getId();
        this.email = user.getEmail();
        this.nickName = user.getNickName();
        this.userRole = user.getRole();
    }
}
