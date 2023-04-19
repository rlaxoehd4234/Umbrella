package com.umbrella.service.Impl;

import com.umbrella.constant.AuthPlatform;
import com.umbrella.constant.Gender;
import com.umbrella.constant.Role;
import com.umbrella.domain.User.User;
import com.umbrella.domain.exception.UserException;
import com.umbrella.exception.DuplicateEmailException;
import com.umbrella.domain.User.UserRepository;
import com.umbrella.security.oAuth2.OAuth2UserInfo;
import com.umbrella.security.oAuth2.factory.OAuth2UserInfoFactory;
import com.umbrella.security.userDetails.UserContext;
import com.umbrella.security.utils.RoleUtil;
import com.umbrella.service.CustomOAuth2UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.*;

import static com.umbrella.domain.exception.UserExceptionType.DUPLICATE_EMAIL_ERROR;

@Service
@RequiredArgsConstructor
public class CustomOAuth2UserServiceImpl implements CustomOAuth2UserService {

    private final UserRepository userRepository;

    private final OAuth2UserInfoFactory oAuth2UserInfoFactory;

    private final RoleUtil roleUtil;

    private static final int OAUTH_USER_AGE = -1;

    private static final String PASSWORD_PREFIX = "OAuth2LoginUserWith";

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {

        OAuth2UserService<OAuth2UserRequest, OAuth2User> delegate = new DefaultOAuth2UserService();
        OAuth2User oAuth2User = delegate.loadUser(userRequest);

        return process(userRequest, oAuth2User);
    }

    private OAuth2User process(OAuth2UserRequest userRequest, OAuth2User oAuth2User) {

        AuthPlatform authPlatform = AuthPlatform.valueOf(userRequest.getClientRegistration()
                                                                        .getRegistrationId().toUpperCase());
        OAuth2UserInfo oAuth2UserInfo = oAuth2UserInfoFactory
                .getOAuth2UserInfo(authPlatform, (Map<String, Object>) oAuth2User.getAttributes());

        String password = PASSWORD_PREFIX + oAuth2UserInfo.getProvider() + UUID.randomUUID();

        Optional<User> findUser = userRepository.findByEmail(oAuth2UserInfo.getEmail());

        if (findUser.isPresent()) {
            if (!String.valueOf(findUser.get().getPlatform()).equals(oAuth2UserInfo.getProvider().toUpperCase())) {
                throw new UserException(DUPLICATE_EMAIL_ERROR);
            } else {
                User user = findUser.get();

                return new UserContext(user.getEmail(), user.getPassword(), user.getId(), user.getNickName(),
                        roleUtil.addAuthoritiesForContext(user), oAuth2User.getAttributes());
            }
        } else {
            User createdUser = createUser(oAuth2UserInfo, password);

            return new UserContext(createdUser.getEmail(), createdUser.getPassword(), createdUser.getId(), createdUser.getNickName(),
                    roleUtil.addAuthoritiesForContext(createdUser), oAuth2User.getAttributes());
        }
    }

    private User createUser(OAuth2UserInfo userInfo, String password) {
        String oAuth2ProviderFullName = userInfo.getProvider() + "_" + userInfo.getProviderId();

        User user = User.builder()
                .email(userInfo.getEmail())
                .password(password)
                .nickName(getOAuth2UserNickname(userInfo))
                .name(oAuth2ProviderFullName)
                .age(OAUTH_USER_AGE)
                .gender(getOAuth2UserGender(userInfo))
                .role(Role.USER)
                .platform(getOAuth2UserPlatform(userInfo))
                .build();

        return userRepository.save(user);
    }

    private String getOAuth2UserNickname(OAuth2UserInfo userInfo) {
        String nickname = userInfo.getName();
        if (nickname.equals("null") || nickname.isBlank()) {
            return "Anonymous #" + UUID.randomUUID();
        }
        return nickname;
    }

    private Gender getOAuth2UserGender(OAuth2UserInfo userInfo) {
        String gender = userInfo.getGender();
        if (gender.equals("null") || gender.isBlank()) {
            return Gender.UNKNOWN;
        }
        return Gender.valueOf(gender.toUpperCase());
    }

    private AuthPlatform getOAuth2UserPlatform(OAuth2UserInfo userInfo) {
        return AuthPlatform.valueOf(userInfo.getProvider().toUpperCase());
    }
}
