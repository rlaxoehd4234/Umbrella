package com.umbrella.security.userDetails;

import com.umbrella.constant.Role;
import com.umbrella.domain.User.User;
import lombok.Builder;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.SpringSecurityCoreVersion;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.util.Assert;

import java.io.Serializable;
import java.util.*;

public class UserContext implements UserDetails, OAuth2User {

    @Getter
    private User user;

    @Getter
    private Map<String, Object> attributes;

    public UserContext(User user) {
        this.user = user;
    }

    public UserContext(User user, Map<String, Object> attributes) {
        this.user = user;
        this.attributes = attributes;
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