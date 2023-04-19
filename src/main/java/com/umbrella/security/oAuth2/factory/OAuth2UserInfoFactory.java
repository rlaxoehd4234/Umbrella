package com.umbrella.security.oAuth2.factory;

import com.umbrella.constant.AuthPlatform;
import com.umbrella.domain.exception.UserException;
import com.umbrella.security.oAuth2.Impl.GithubUserInfo;
import com.umbrella.security.oAuth2.Impl.GoogleUserInfo;
import com.umbrella.security.oAuth2.Impl.KakaoUserInfo;
import com.umbrella.security.oAuth2.Impl.NaverUserInfo;
import com.umbrella.security.oAuth2.OAuth2UserInfo;
import org.springframework.stereotype.Component;

import java.util.Map;

import static com.umbrella.domain.exception.UserExceptionType.UNSUPPORTED_PLATFORM;

@Component
public class OAuth2UserInfoFactory {
    public OAuth2UserInfo getOAuth2UserInfo(AuthPlatform authPlatform, Map<String, Object> attributes) {
        switch (authPlatform) {
            case KAKAO: return new KakaoUserInfo(attributes);
            case GOOGLE: return new GoogleUserInfo(attributes);
            case GITHUB: return new GithubUserInfo(attributes);
            case NAVER: return new NaverUserInfo(attributes);
            default: throw new UserException(UNSUPPORTED_PLATFORM);
        }
    }
}
