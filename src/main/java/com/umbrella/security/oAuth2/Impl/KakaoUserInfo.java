package com.umbrella.project_umbrella.security.oAuth2.Impl;

import com.umbrella.project_umbrella.security.oAuth2.OAuth2UserInfo;

import java.util.Map;

public class KakaoUserInfo extends OAuth2UserInfo {

    private Map<String, Object> attributesAccount;

    public KakaoUserInfo(Map<String, Object> attributes) {
        super(attributes);
        this.attributesAccount = (Map<String, Object>) attributes.get("kakao_account");
    }

    @Override
    public String getProviderId() {
        return String.valueOf(attributes.get("id"));
    }

    @Override
    public String getProvider() {
        return "kakao";
    }

    @Override
    public String getEmail() {
        return String.valueOf(attributesAccount.get("email"));
    }

    @Override
    public String getName() {
        return String.valueOf(attributesAccount.get("name"));
    }

    @Override
    public String getGender() {
        return String.valueOf(attributesAccount.get("gender"));
    }

    @Override
    public String getBirthday() {
        return String.valueOf(attributesAccount.get("birthday"));
    }

    @Override
    public String getBirthYear() {
        return null;
    }
}
