package com.umbrella.project_umbrella.security.oAuth2.factory;

import com.umbrella.project_umbrella.constant.AuthPlatform;
import com.umbrella.project_umbrella.security.oAuth2.Impl.GithubUserInfo;
import com.umbrella.project_umbrella.security.oAuth2.Impl.GoogleUserInfo;
import com.umbrella.project_umbrella.security.oAuth2.Impl.KakaoUserInfo;
import com.umbrella.project_umbrella.security.oAuth2.Impl.NaverUserInfo;
import com.umbrella.project_umbrella.security.oAuth2.OAuth2UserInfo;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class OAuth2UserInfoFactory {
    public OAuth2UserInfo getOAuth2UserInfo(AuthPlatform authPlatform, Map<String, Object> attributes) {
        switch (authPlatform) {
            case KAKAO: return new KakaoUserInfo(attributes);
            case GOOGLE: return new GoogleUserInfo(attributes);
            case GITHUB: return new GithubUserInfo(attributes);
            case NAVER: return new NaverUserInfo(attributes);
            default: throw new IllegalArgumentException("소셜 로그인을 지원하지 않는 플랫폼입니다.");
        }
    }
}
