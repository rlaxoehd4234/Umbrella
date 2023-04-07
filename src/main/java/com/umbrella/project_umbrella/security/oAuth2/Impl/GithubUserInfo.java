package com.umbrella.project_umbrella.security.oAuth2.Impl;

import com.umbrella.project_umbrella.security.oAuth2.OAuth2UserInfo;

import java.util.Map;
import java.util.UUID;

public class GithubUserInfo extends OAuth2UserInfo {

    private final String GITHUB_PROXY_EMAIL = "octocat@github.com" + UUID.randomUUID();

    public GithubUserInfo(Map<String, Object> attributes) {
        super(attributes);
    }

    @Override
    public String getProviderId() {
        return String.valueOf(attributes.get("id"));
    }

    @Override
    public String getProvider() {
        return "github";
    }

    @Override
    // Github Profile 의 이메일 설정이 private 으로 되어있으면 null 값이 들어옴
    public String getEmail() {
        String githubEmail = String.valueOf(attributes.get("email"));
        if (githubEmail.equals("null") || githubEmail.isBlank()) {
            return GITHUB_PROXY_EMAIL;
        }
        return githubEmail;
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
