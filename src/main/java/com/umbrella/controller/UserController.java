package com.umbrella.controller;

import com.umbrella.dto.user.*;
import com.umbrella.dto.workspace.WorkspaceRequestCreateDto;
import com.umbrella.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequiredArgsConstructor
@Slf4j
public class UserController {

    private final UserService userService;

    @PostMapping(value = "/signUp")
    public ResponseEntity<String> signUp(@Valid @RequestBody UserRequestSignUpDto userSignUpDto) {
        validateSignUpRequest(userSignUpDto);
        userService.signUp(userSignUpDto);
        return ResponseEntity.ok().body("회원가입이 성공적으로 완료되었습니다!");
    }

    private void validateSignUpRequest(UserRequestSignUpDto userSignUpDto) {
        /* AOP Exception Handler will Operate */
        Assert.hasText(userSignUpDto.getEmail(), "email must not be blank");
        Assert.hasText(userSignUpDto.getNickName(), "nickName must not be blank");
        Assert.hasText(userSignUpDto.getPassword(), "password must not be blank");
        Assert.hasText(userSignUpDto.getName(), "mName must not be blank");
        Assert.hasText(userSignUpDto.getBirthDate(), "birthDate must not be null");
        Assert.hasText(userSignUpDto.getGender().getGenderValue(), "gender must not be blank");
    }

    @PutMapping(value = "/user/update/info")
    public ResponseEntity<String> updateUserInfo(@Valid @RequestBody UserUpdateDto userUpdateDto) {
        userService.update(userUpdateDto);
        return ResponseEntity.ok().body("입력하신 정보로 성공적으로 수정되었습니다.");
    }

    @PatchMapping(value = "/user/update/password")
    public ResponseEntity<String> updateUserPassword(@Valid @RequestBody UpdatePasswordDto updatePasswordDto) {
        /* AOP Exception Handler will Operate */
        Assert.hasText(updatePasswordDto.getCheckPassword(), "checkPassword must not be blank");
        Assert.hasText(updatePasswordDto.getNewPassword(), "newPassword must not be blank");

        userService.updatePassword(updatePasswordDto.getCheckPassword(), updatePasswordDto.getNewPassword());
        return ResponseEntity.ok().body("입력하신 비밀번호로 성공적으로 수정되었습니다.");
    }

    @DeleteMapping(value = "/user/withdraw")
    public ResponseEntity<String> withdraw(@Valid @RequestBody WithdrawUserDto withdrawUserDto) {
        /* AOP Exception Handler will Operate */
        Assert.hasText(withdrawUserDto.getPassword(), "password must not be blank");

        userService.withdraw(withdrawUserDto.getPassword());
        return ResponseEntity.ok().body("성공적으로 회원탈퇴가 진행되었습니다.");
    }

    @PostMapping(value = "/user/{userId}/info")
    public ResponseEntity getInfo(@Valid @PathVariable("userId") Long id) {
        UserInfoDto userInfoDto = userService.getInfo(id);

        return new ResponseEntity(userInfoDto, HttpStatus.OK);
    }

    @GetMapping(value = "/user/info")
    public ResponseEntity getMyInfo() {
        UserInfoDto userInfoDto = userService.getMyInfo();

        return new ResponseEntity(userInfoDto, HttpStatus.OK);
    }

    @PostMapping(value = "/workspace/create")
    public ResponseEntity createWorkspace(@RequestBody WorkspaceRequestCreateDto workspaceRequestCreateDto) {
        userService.createWorkspace(workspaceRequestCreateDto);

        return ResponseEntity.ok().body("워크스페이스가 성공적으로 생성되었습니다.");
    }
}
