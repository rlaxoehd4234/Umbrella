package com.umbrella.project_umbrella.security.oAuth2.Impl;

import com.umbrella.project_umbrella.security.oAuth2.OAuth2UserInfo;

import java.util.Map;

public class GoogleUserInfo extends OAuth2UserInfo {


    public GoogleUserInfo(Map<String, Object> attributes) {
        super(attributes);
    }

    @Override
    public String getProviderId() {
        return String.valueOf(attributes.get("sub"));
    }

    @Override
    public String getProvider() {
        return "google";
    }

    @Override
    public String getEmail() {
        return String.valueOf(attributes.get("email"));
    }

    @Override
    public String getName() {
        return String.valueOf(attributes.get("name"));
    }

    @Override
    public String getGender() {
        return String.valueOf(attributes.get("gender"));
    }

    @Override
    public String getBirthday() {
        return String.valueOf(attributes.get("birthday"));
    }

    @Override
    public String getBirthYear() {
        return null;
    }
}
