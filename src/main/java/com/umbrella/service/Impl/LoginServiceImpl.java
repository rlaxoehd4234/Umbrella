package com.umbrella.service.Impl;

import com.umbrella.domain.User.User;
import com.umbrella.domain.User.UserRepository;
import com.umbrella.domain.exception.UserException;
import com.umbrella.security.userDetails.UserContext;
import com.umbrella.security.utils.RoleUtil;
import com.umbrella.service.LoginService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import static com.umbrella.domain.exception.UserExceptionType.UNMATCHED_LOGIN_INFO_ERROR;

@Service
@RequiredArgsConstructor
public class LoginServiceImpl implements LoginService {

    private final UserRepository userRepository;

    private final RoleUtil roleUtil;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        User user = userRepository.findByEmail(email)
                        .orElseThrow(
                            () -> new UserException(UNMATCHED_LOGIN_INFO_ERROR)
                        );
        return new UserContext(
                user.getEmail(),
                user.getPassword(),
                user.getId(),
                user.getNickName(),
                roleUtil.addAuthoritiesForContext(user));
    }
}
