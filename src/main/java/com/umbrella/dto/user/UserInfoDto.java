package com.umbrella.dto.user;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.umbrella.domain.User.User;
import lombok.Builder;
import lombok.Getter;

@Getter
public class UserInfoDto {

    private final String email;

    private final String name;

    @JsonProperty("nick_name")
    private final String nickName;

    private final Integer age;

    @Builder
    public UserInfoDto(User findUser) {
        this.email = findUser.getEmail();
        this.name = findUser.getName();
        this.nickName = findUser.getNickName();
        this.age = findUser.getAge();
    }
}
