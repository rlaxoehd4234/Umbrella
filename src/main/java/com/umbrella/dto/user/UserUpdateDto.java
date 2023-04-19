package com.umbrella.dto.user;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Getter;

import java.util.Optional;

@Getter
public class UserUpdateDto {

    @JsonProperty("nick_name")
    private Optional<String> nickName;

    private Optional<String> name;

    private Optional<Integer> age;

    @Builder
    public UserUpdateDto(Optional<String> nickName,
                         Optional<String> name,
                         Optional<Integer> age) {
        this.nickName = nickName;
        this.name = name;
        this.age = age;
    }
}
