package com.umbrella.dto.user;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Optional;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class UserRequestUpdateDto {

    @Getter
    @JsonProperty("nick_name")
    private Optional<String> nickName;

    @Getter
    private Optional<String> name;

    @Getter
    private Optional<Integer> age;

    @Builder
    public UserRequestUpdateDto(Optional<String> nickName,
                                Optional<String> name,
                                Optional<Integer> age) {
        this.nickName = nickName;
        this.name = name;
        this.age = age;
    }
}
