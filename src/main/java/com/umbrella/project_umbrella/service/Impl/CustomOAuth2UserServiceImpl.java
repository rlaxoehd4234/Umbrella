package com.umbrella.project_umbrella.service.Impl;

import com.umbrella.project_umbrella.constant.AuthPlatform;
import com.umbrella.project_umbrella.constant.Gender;
import com.umbrella.project_umbrella.constant.Role;
import com.umbrella.project_umbrella.domain.User.User;
import com.umbrella.project_umbrella.exception.DuplicateEmailException;
import com.umbrella.project_umbrella.repository.UserRepository;
import com.umbrella.project_umbrella.security.oAuth2.OAuth2UserInfo;
import com.umbrella.project_umbrella.security.oAuth2.factory.OAuth2UserInfoFactory;
import com.umbrella.project_umbrella.security.userDetails.UserContext;
import com.umbrella.project_umbrella.service.CustomOAuth2UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CustomOAuth2UserServiceImpl implements CustomOAuth2UserService {

    private final UserRepository userRepository;

    private final OAuth2UserInfoFactory oAuth2UserInfoFactory;

    private static final int OAUTH_USER_AGE = -1;

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

        String password = "OAuth2LoginUserWith" + oAuth2UserInfo.getProvider() + UUID.randomUUID();

        Optional<User> findUser = userRepository.findByEmail(oAuth2UserInfo.getEmail());

        if (findUser.isPresent()) {
            if (!String.valueOf(findUser.get().getPlatform()).equals(oAuth2UserInfo.getProvider().toUpperCase())) {
                throw new DuplicateEmailException("This email has already been registered!");
            }
        }

        return findUser.map(user -> new UserContext(user, oAuth2User.getAttributes()))
                .orElseGet(() -> new UserContext(createUser(oAuth2UserInfo, password), oAuth2User.getAttributes()));
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
