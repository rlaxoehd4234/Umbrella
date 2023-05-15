package com.umbrella.service.Impl;

import com.umbrella.domain.User.User;
import com.umbrella.domain.User.UserRepository;
import com.umbrella.domain.exception.UserException;
import com.umbrella.security.utils.CookieUtil;
import com.umbrella.service.JwtService;

import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.AccessLevel;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.security.Key;
import java.util.*;

import static com.umbrella.domain.exception.UserExceptionType.NOT_FOUND_ERROR;

@Transactional
@Service
@Setter(AccessLevel.PRIVATE)
@Slf4j
public class JwtServiceImpl implements JwtService {

    @Value("${jwt.access.expiration}")
    private long accessTokenExpiration;

    @Value("${jwt.refresh.expiration}")
    private long refreshTokenExpiration;

    @Value("${jwt.access.header}")
    private String accessHeader;

    @Value("${jwt.refresh.header}")
    private String refreshHeader;

    private final UserRepository userRepository;

    private final Key secretKey;

    private final String COOKIE_REFRESH_TOKEN_KEY;

    private final CookieUtil cookieUtil;

    private static final String EMAIL_CLAIM = "email";
    private static final String NICK_NAME_CLAIM = "nickName";
    private static final String BEARER = "Bearer ";
    private static final String ISSUER = "Umbrella";
    private static final SignatureAlgorithm SIGNATURE_ALGORITHM = SignatureAlgorithm.HS512;


    public JwtServiceImpl(UserRepository userRepository,
                          @Value("${jwt.secret}") String secret,
                          @Value("${app.auth.cookie.refresh-cookie-key}") String cookieKey,
                          CookieUtil cookieUtil) {
        this.userRepository = userRepository;
        byte[] keyBytes = Decoders.BASE64.decode(secret);
        this.secretKey = Keys.hmacShaKeyFor(keyBytes);
        this.COOKIE_REFRESH_TOKEN_KEY = cookieKey;
        this.cookieUtil = cookieUtil;
    }

    @Override
    public String createAccessToken(String email, String nickName) {
        return Jwts.builder()
                .signWith(secretKey, SIGNATURE_ALGORITHM)
                .setSubject(email)
                .setIssuer(ISSUER)
                .setExpiration(new Date(System.currentTimeMillis() + accessTokenExpiration * 1000))
                .claim(EMAIL_CLAIM, email)
                .claim(NICK_NAME_CLAIM, nickName)
                .compact();
    }

    @Override
    public String createRefreshToken(String email) {

        return Jwts.builder()
                    .signWith(secretKey, SIGNATURE_ALGORITHM)
                    .setSubject(email)
                    .setIssuer(ISSUER)
                    .setExpiration(new Date(System.currentTimeMillis() + refreshTokenExpiration * 1000))
                    .compact();
    }

    @Override
    public void updateRefreshToken(String email, String refreshToken) {
        userRepository.findByEmail(email).ifPresentOrElse(
                user -> user.updateRefreshToken(refreshToken),
                () -> {
                    throw new UserException(NOT_FOUND_ERROR);
                }
        );
    }

    @Override
    public void destroyRefreshToken(String email) {
        userRepository.findByEmail(email).ifPresentOrElse(
                User::destroyRefreshToken,
                () -> {
                    throw new UserException(NOT_FOUND_ERROR);
                }
        );
    }

    @Override
    public void sendAccessAndRefreshToken(HttpServletResponse response, String accessToken, String refreshToken) {
        response.setContentType("application/json;charset=UTF-8");
        response.setStatus(HttpServletResponse.SC_OK);

        setAccessTokenHeader(response, accessToken);
        setRefreshTokenHeader(response, refreshToken);
    }

    @Override
    public void setRefreshTokenInCookie(HttpServletResponse response, String refreshToken) {
        ResponseCookie cookie = ResponseCookie.from(COOKIE_REFRESH_TOKEN_KEY, refreshToken)
                .httpOnly(true)
                .secure(true)
                .sameSite("None")
                .maxAge(refreshTokenExpiration)
                .path("/")
                .build();

        response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());
    }

    @Override
    public void sendAccessToken(HttpServletResponse response, String accessToken) {
        response.setContentType("application/json;charset=UTF-8");
        setAccessTokenHeader(response, accessToken);
    }

    @Override
    public Optional<String> extractAccessToken(HttpServletRequest request) {
        return Optional.ofNullable(request.getHeader(accessHeader))
                .filter(accessToken -> accessToken.startsWith(BEARER))
                .map(accessToken -> accessToken.replace(BEARER, ""));
    }

    @Override
    public Optional<String> extractRefreshToken(HttpServletRequest request) {
        Optional<Cookie> cookie = cookieUtil.getCookie(request, COOKIE_REFRESH_TOKEN_KEY);
        return cookie.map(Cookie::getValue);
    }

    @Override
    public Optional<String> extractEmail(String token) {
        return extractClaim(token, EMAIL_CLAIM, String.class);
    }

    @Override
    public Optional<String> extractNickName(String token) {
        return extractClaim(token, NICK_NAME_CLAIM, String.class);
    }

    @Override
    public Optional<String> extractSubject(String token) {
        return extractClaim(token, Claims.SUBJECT, String.class);
    }

    private <T> Optional<T> extractClaim(String token, String claimName, Class<T> type) {
        try {
            Claims claims = extractClaim(token);
            return Optional.ofNullable(claims.get(claimName, type));
        } catch (ExpiredJwtException e) {
            Claims claims = e.getClaims();
            return Optional.ofNullable(claims.get(claimName, type));
        } catch (JwtException | IllegalArgumentException e) {
            throw new JwtException("유효하지 않은 토큰입니다.");
        }
    }

    @Override
    public void setAccessTokenHeader(HttpServletResponse response, String accessToken) {
        response.setHeader(accessHeader, accessToken);
    }

    @Override
    public void setRefreshTokenHeader(HttpServletResponse response, String refreshToken) {
        response.setHeader(refreshHeader, refreshToken);
    }

    @Override
    public int isTokenValid(String token) {
        try {
            Jwts.parserBuilder().setSigningKey(secretKey)
                    .build().parseClaimsJws(token);
            return 1;
        } catch (ExpiredJwtException e) {
            log.error("만료된 토큰입니다.", e);
            return 0;
        } catch (JwtException | IllegalArgumentException e) {
            log.error("유효하지 않은 토큰입니다.", e);
            return -1;
        }
    }

    private Claims extractClaim(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(secretKey)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }
}
