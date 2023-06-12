package com.umbrella.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.umbrella.domain.User.UserRepository;
import com.umbrella.security.login.cookie.CookieOAuth2AuthorizationRequestRepository;
import com.umbrella.security.login.filter.JsonEmailPasswordAuthenticationFilter;
import com.umbrella.security.login.filter.JwtAuthenticationProcessingFilter;
import com.umbrella.security.login.filter.JwtExceptionFilter;
import com.umbrella.security.login.handler.*;
import com.umbrella.security.utils.RoleUtil;
import com.umbrella.service.CustomOAuth2UserService;
import com.umbrella.service.JwtService;
import com.umbrella.service.LoginService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

@Configuration
@RequiredArgsConstructor
public class SecurityConfig {

    private final ObjectMapper objectMapper;

    private final LoginService loginService;

    private final UserRepository userRepository;

    private final JwtService jwtService;

    private final CustomOAuth2UserService customOAuth2UserService;

    private final CookieOAuth2AuthorizationRequestRepository cookieOAuth2AuthorizationRequestRepository;

    private final RoleUtil roleUtil;

    @Bean
    public WebSecurityCustomizer webSecurityCustomizer() {
        return (web) -> web.ignoring().antMatchers(HttpMethod.POST, "/login", "/signUp", "/", "/send-email",
                "/auth/**", "/oauth2/**");
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .formLogin().disable()
                .httpBasic().disable()
                .csrf().disable()
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
        .and()
                .authorizeRequests()
                .anyRequest().authenticated()
        .and()
                .addFilterAfter(jsonEmailPasswordAuthenticationFilter(), UsernamePasswordAuthenticationFilter.class)
                .addFilterBefore(jwtAuthenticationProcessingFilter(), JsonEmailPasswordAuthenticationFilter.class)
                .addFilterBefore(jwtExceptionFilter(), JwtAuthenticationProcessingFilter.class)
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
                .logout()
                .logoutUrl("/logout")
                .logoutSuccessUrl("/login")
                .deleteCookies("refresh")
                .logoutSuccessHandler(logoutSuccessHandler())
        .and()
                .cors().configurationSource(corsConfigurationSource());
//        .and()
//                .exceptionHandling()
//                .authenticationEntryPoint(new HttpStatusEntryPoint(HttpStatus.FORBIDDEN));

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
        return new LoginSuccessJWTProvideHandler(jwtService, userRepository, objectMapper);
    }

    @Bean
    public LoginFailureHandler loginFailureHandler(){
        return new LoginFailureHandler();
    }

    @Bean
    public LogoutSuccessHandler logoutSuccessHandler() {
        return new LogoutSuccessHandler(objectMapper);
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
        return new JwtAuthenticationProcessingFilter(jwtService, userRepository, roleUtil);
    }

    @Bean
    public JwtExceptionFilter jwtExceptionFilter() {
        return new JwtExceptionFilter();
    }

    @Bean
    public OAuth2LoginSuccessHandler oAuth2LoginSuccessHandler() {
        return new OAuth2LoginSuccessHandler(userRepository, jwtService, cookieOAuth2AuthorizationRequestRepository);
    }

    @Bean
    public OAuth2LoginFailureHandler oAuth2LoginFailureHandler() {
        return new OAuth2LoginFailureHandler();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();

        configuration.addAllowedOriginPattern("*");
        configuration.addAllowedHeader("*");
        configuration.addAllowedMethod("*");
        configuration.addExposedHeader("*");
        configuration.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}
