package com.umbrella.domain.User;

import com.umbrella.constant.AuthPlatform;
import com.umbrella.constant.Gender;
import com.umbrella.constant.Role;
import com.umbrella.domain.WorkSpace.WorkspaceUser;
import com.umbrella.dto.user.UserRequestUpdateDto;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.util.Assert;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class User {
    @Id
    @Column(name = "user_id")
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false, unique = true)
    private String nickName;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false)
    private String name;

    @Column
    private int age;

    @Column // UNKNOWN -> OAuth2 서버측에서 성별 값을 전달받지 못했을 경우
    private Gender gender;

    @Column
    @Enumerated(EnumType.STRING)
    private Role role;

    @Column
    @Enumerated(EnumType.STRING)
    private AuthPlatform platform;

    @Column(length = 100)
    @Lob
    private String refreshToken;

    @OneToMany(mappedBy = "workspaceUser")
    private List<WorkspaceUser> workspaceUsers = new ArrayList<>();

    @Builder
    public User(String email, String nickName, String password,
                    String name, Integer age, Gender gender, Role role, AuthPlatform platform) {

        Assert.hasText(email, "email must not be blank");
        Assert.hasText(nickName, "nickName must not be blank");
        Assert.hasText(password, "password must not be blank");
        Assert.hasText(name, "name must not be blank");
        Assert.notNull(age, "age must not be null");
        Assert.notNull(gender, "gender must not be null");

        this.email = email;
        this.nickName = nickName;
        this.password = password;
        this.name = name;
        this.age = age;
        this.gender = gender;
        this.role = role;
        this.platform = platform;
    }

    public void updateUser(UserRequestUpdateDto userUpdateDto) {
        userUpdateDto.getNickName().ifPresent(
                nickName -> this.nickName = nickName
        );

        userUpdateDto.getName().ifPresent(
                mName -> this.name = mName
        );

        userUpdateDto.getAge().ifPresent(
                age -> this.age = age
        );
    }

    public void updatePassword(PasswordEncoder passwordEncoder, String password) {
        this.password = passwordEncoder.encode(password);
    }

    public void updateRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }

    public void destroyRefreshToken() {
        this.refreshToken = null;
    }

    public void encodePassword(PasswordEncoder passwordEncoder) {
        this.password = passwordEncoder.encode(password);
    }

    public boolean matchPassword(PasswordEncoder passwordEncoder, String checkPassword) {
        return passwordEncoder.matches(checkPassword, getPassword());
    }

    public void addUserAuthorities() {
        this.role = Role.USER;
    }

    public void addDefaultPlatform() { this.platform = AuthPlatform.UMBRELLA; }

    public void enterWorkspaceUser(WorkspaceUser workspaceUser) {
        workspaceUser.setWorkspaceUser(this);
        this.workspaceUsers.add(workspaceUser);
    }

    public void exitWorkspaceUser(WorkspaceUser workspaceUser) {
        workspaceUser.getWorkspace().getWorkspaceUsers().remove(workspaceUser);
        this.getWorkspaceUsers().remove(workspaceUser);
    }
}
