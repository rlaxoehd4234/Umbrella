package com.umbrella.dto.user;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class UserResponseUpdateDto {

    @Getter
    @JsonProperty("nick_name")
    private String nickName;

    @Getter
    private String name;

    @Getter
    private int age;

    @Builder
    public UserResponseUpdateDto(String nickName,
                                String name,
                                int age) {
        this.nickName = nickName;
        this.name = name;
        this.age = age;
    }
}
