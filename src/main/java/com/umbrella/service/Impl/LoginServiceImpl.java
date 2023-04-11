package com.umbrella.service.Impl;

import com.umbrella.domain.User.User;
import com.umbrella.domain.User.UserRepository;
import com.umbrella.security.userDetails.UserContext;
import com.umbrella.security.utils.RoleUtil;
import com.umbrella.service.LoginService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class LoginServiceImpl implements LoginService {

    private final UserRepository userRepository;

    private final RoleUtil roleUtil;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        User user = userRepository.findByEmail(email)
                        .orElseThrow(
                            () -> new UsernameNotFoundException("해당 이메일을 가진 계정이 존재하지 않습니다.")
                        );

        return new UserContext(user.getEmail(), user.getPassword(), user.getId(), user.getNickName(), roleUtil.addAuthoritiesForContext(user));
    }
}
