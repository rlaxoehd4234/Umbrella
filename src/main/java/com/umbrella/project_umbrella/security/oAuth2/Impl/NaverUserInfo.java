package com.umbrella.project_umbrella.security.oAuth2.Impl;

import com.umbrella.project_umbrella.security.oAuth2.OAuth2UserInfo;

import java.util.Map;

public class NaverUserInfo extends OAuth2UserInfo {

    private Map<String, Object> attributeResponse;

    public NaverUserInfo(Map<String, Object> attributes) {
        super(attributes);
        this.attributeResponse = (Map<String, Object>) attributes.get("response");
    }

    @Override
    public String getProviderId() {
        return String.valueOf(attributeResponse.get("id"));
    }

    @Override
    public String getProvider() {
        return "naver";
    }

    @Override
    public String getEmail() {
        return String.valueOf(attributeResponse.get("email"));
    }

    @Override
    public String getName() {
        return String.valueOf(attributeResponse.get("name"));
    }

    @Override
    public String getGender() {
        String gender = String.valueOf(attributeResponse.get("gender"));
        if (gender.equals("M")) {
            gender = "MALE";
        } else if (gender.equals("F")) {
            gender = "FEMALE";
        } else {
            throw new IllegalArgumentException("등록되어 있지 않은 성별입니다!");
        }

        return gender;
    }

    @Override
    public String getBirthday() {
        return String.valueOf(attributeResponse.get("birthday"));
    }

    @Override
    public String getBirthYear() {
        return String.valueOf(attributeResponse.get("birthyear"));
    }
}
