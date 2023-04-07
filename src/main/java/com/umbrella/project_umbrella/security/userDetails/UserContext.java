package com.umbrella.project_umbrella.security.userDetails;

import com.umbrella.project_umbrella.constant.Role;
import com.umbrella.project_umbrella.domain.User.User;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.util.*;

public class UserContext implements UserDetails, OAuth2User {

    private User user;

    @Getter
    private Long id;

    private Map<String, Object> attributes;

    public UserContext(User user) {
        this.user = user;
        this.id = user.getId();
    }

    public UserContext(User user, Map<String, Object> attributes) {
        this.user = user;
        this.id = user.getId();
        this.attributes = attributes;
    }

    @Override
    public Map<String, Object> getAttributes() {
        return this.attributes;
    }

    @Override
    public String getName() {
        return (String) attributes.get("name");
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        Collection<GrantedAuthority> collect = new HashSet<>();
        user.addUserAuthorities();
        collect.add((GrantedAuthority) () -> user.getRole().toString());
        return collect;
    }

    @Override
    public String getPassword() {
        return user.getPassword();
    }

    @Override
    public String getUsername() {
        return user.getEmail();
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

}