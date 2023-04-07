package com.umbrella.project_umbrella.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.umbrella.project_umbrella.repository.UserRepository;
import com.umbrella.project_umbrella.security.login.cookie.CookieOAuth2AuthorizationRequestRepository;
import com.umbrella.project_umbrella.security.login.filter.JsonEmailPasswordAuthenticationFilter;
import com.umbrella.project_umbrella.security.login.filter.JwtAuthenticationProcessingFilter;
import com.umbrella.project_umbrella.security.login.handler.LoginFailureHandler;
import com.umbrella.project_umbrella.security.login.handler.LoginSuccessJWTProvideHandler;
import com.umbrella.project_umbrella.security.login.handler.OAuth2LoginFailureHandler;
import com.umbrella.project_umbrella.security.login.handler.OAuth2LoginSuccessHandler;
import com.umbrella.project_umbrella.service.CustomOAuth2UserService;
import com.umbrella.project_umbrella.service.JwtService;
import com.umbrella.project_umbrella.service.LoginService;
import com.umbrella.project_umbrella.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.HttpStatusEntryPoint;
import org.springframework.security.web.authentication.logout.LogoutFilter;

import javax.persistence.EntityManager;

@Configuration
@RequiredArgsConstructor
public class SecurityConfig {

    private final ObjectMapper objectMapper;

    private final LoginService loginService;

    private final UserRepository userRepository;

    private final JwtService jwtService;

    private final CustomOAuth2UserService customOAuth2UserService;

    private final CookieOAuth2AuthorizationRequestRepository cookieOAuth2AuthorizationRequestRepository;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .formLogin().disable()
                .httpBasic().disable()
                .csrf().disable()
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
        .and()
                .authorizeRequests()
                .antMatchers("/login", "/signUp", "/").permitAll()
                .antMatchers("/oauth2/**", "/auth/**").permitAll()
                .anyRequest().authenticated()
        .and()
                .addFilterAfter(jsonEmailPasswordAuthenticationFilter(), LogoutFilter.class)
                .addFilterBefore(jwtAuthenticationProcessingFilter(), JsonEmailPasswordAuthenticationFilter.class)
                .oauth2Login()
                    .authorizationEndpoint()
                    .authorizationRequestRepository(cookieOAuth2AuthorizationRequestRepository)
                .and()
                    .redirectionEndpoint().baseUri("/login/oauth2/code/*")
                .and()
                    .userInfoEndpoint(
                            userInfo -> userInfo.userService(customOAuth2UserService)
                                    .and()
                                        .successHandler((oAuth2LoginSuccessHandler()))
                                        .failureHandler(oAuth2LoginFailureHandler())
                    )
        .and()
                .exceptionHandling()
                .authenticationEntryPoint(new HttpStatusEntryPoint(HttpStatus.FORBIDDEN));

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return PasswordEncoderFactories.createDelegatingPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager() {
        DaoAuthenticationProvider daoAuthenticationProvider = new DaoAuthenticationProvider();
        daoAuthenticationProvider.setPasswordEncoder(passwordEncoder());
        daoAuthenticationProvider.setUserDetailsService(loginService);

        return new ProviderManager(daoAuthenticationProvider);
    }

    @Bean
    public LoginSuccessJWTProvideHandler loginSuccessJWTProvideHandler(){
        return new LoginSuccessJWTProvideHandler(jwtService, userRepository);
    }

    @Bean
    public LoginFailureHandler loginFailureHandler(){
        return new LoginFailureHandler();
    }

    @Bean
    public JsonEmailPasswordAuthenticationFilter jsonEmailPasswordAuthenticationFilter() {
        JsonEmailPasswordAuthenticationFilter jsonEmailPasswordAuthenticationFilter =
                new JsonEmailPasswordAuthenticationFilter(objectMapper);

        jsonEmailPasswordAuthenticationFilter.setAuthenticationManager(authenticationManager());
        jsonEmailPasswordAuthenticationFilter.setAuthenticationSuccessHandler(loginSuccessJWTProvideHandler());
        jsonEmailPasswordAuthenticationFilter.setAuthenticationFailureHandler(loginFailureHandler());

        return jsonEmailPasswordAuthenticationFilter;
    }

    @Bean
    public JwtAuthenticationProcessingFilter jwtAuthenticationProcessingFilter() {
        JwtAuthenticationProcessingFilter jwtAuthenticationProcessingFilter
                                    = new JwtAuthenticationProcessingFilter(jwtService, userRepository);

        return jwtAuthenticationProcessingFilter;
    }

    @Bean
    public OAuth2LoginSuccessHandler oAuth2LoginSuccessHandler() {
        OAuth2LoginSuccessHandler oAuth2LoginSuccessHandler = new OAuth2LoginSuccessHandler();
        return oAuth2LoginSuccessHandler;
    }

    @Bean
    public OAuth2LoginFailureHandler oAuth2LoginFailureHandler() {
        OAuth2LoginFailureHandler oAuth2LoginFailureHandler = new OAuth2LoginFailureHandler();
        return oAuth2LoginFailureHandler;
    }
}
