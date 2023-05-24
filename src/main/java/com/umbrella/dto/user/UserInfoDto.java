package com.umbrella.dto.user;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.umbrella.domain.User.User;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class UserInfoDto {

    private String email;

    private String name;

    @JsonProperty("nick_name")
    private String nickName;

    private Integer age;

    @Builder
    public UserInfoDto(User findUser) {
        this.email = findUser.getEmail();
        this.name = findUser.getName();
        this.nickName = findUser.getNickName();
        this.age = findUser.getAge();
    }
}
