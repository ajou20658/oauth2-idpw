package com.login.login.domain.service.jwt;

import com.login.login.common.exception.ControllerMessage;
import com.login.login.common.exception.CustomException;
import com.login.login.domain.model.jwt.JwtAttributes;
import com.login.login.infrastructure.cache.redis.RedisService;
import com.login.login.infrastructure.entity.jwt.RefreshTokenRepository;
import com.login.login.infrastructure.entity.member.Member;
import com.login.login.infrastructure.entity.member.MemberRepository;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import javax.crypto.SecretKey;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Slf4j
public class JwtTokenProvider {
    private final MemberRepository memberRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final RedisService redisService;
    public static final String AUTHORIZATION_HEADER = "Authorization";
    public static final String BEARER_PREFIX = "Bearer ";
    public static final String AUTHORITIES_KEY="auth";
    private final SecretKey secretKey;
    @Value("${jwt.validTime}")
    private Long validTime;
    @Value("${jwt.refreshTime}")
    private Long refreshTime;
    public JwtTokenProvider(
            @Value("${jwt.secret}") String secretKey,
            MemberRepository memberRepository,
            RefreshTokenRepository refreshTokenRepository,
            RedisService redisService){
        this.secretKey = Keys.hmacShaKeyFor(secretKey.getBytes());
        this.memberRepository = memberRepository;
        this.refreshTokenRepository = refreshTokenRepository;
        this.redisService = redisService;
    }
    @Transactional
    public JwtAttributes generateToken(Authentication authentication ){
        OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();
        Map<String, Object> attributes = oAuth2User.getAttributes();
        String authorities = authentication.getAuthorities().stream().map(GrantedAuthority::getAuthority).collect(Collectors.joining(","));
        Date now = new Date();
        String userId = String.valueOf(attributes.get("id"));
        String access = Jwts.builder()
                .subject(userId)
                .claim(AUTHORITIES_KEY,authorities)
                .issuedAt(now)
                .expiration(new Date(now.getTime()+validTime))
                .signWith(secretKey)
                .compact();
        String refresh = Jwts.builder()
                .subject(userId)
                .issuedAt(now)
                .expiration(new Date(now.getTime()+refreshTime))
                .signWith(secretKey)
                .compact();
        updateRefreshToken(Long.valueOf(userId),refresh);
        return JwtAttributes.builder()
                .userId(Long.valueOf(userId))
                .access(access)
                .refresh(refresh)
                .build();
    }
    @Transactional
    public JwtAttributes jwtRefresh(String refreshToken){ //깔끔한 토큰이 전달되었다고 가정
        log.info(refreshToken);
        if(!validateToken(refreshToken)){
            throw new CustomException(ControllerMessage.EXPIRED_REFRESH_TOKEN);
        }
        Claims claims = parse(refreshToken);
        String userId = claims.getSubject();
        if(!memberRepository.existsMemberByIdAndRefreshToken(Long.valueOf(userId),refreshToken)){
            throw new CustomException(ControllerMessage.BAD_REQUEST);
        }
        Date now = new Date();
        String access = Jwts.builder()
                .subject(userId)
                .claim(AUTHORITIES_KEY,claims.get(AUTHORITIES_KEY))
                .issuedAt(now)
                .expiration(new Date(now.getTime()+validTime))
                .signWith(secretKey)
                .compact();
        String refresh = Jwts.builder()
                .subject(userId)
                .issuedAt(now)
                .expiration(new Date(now.getTime()+refreshTime))
                .signWith(secretKey)
                .compact();
        updateRefreshToken(Long.valueOf(userId),refresh);
        return JwtAttributes.builder()
                .userId(Long.valueOf(userId))
                .access(access)
                .refresh(refresh)
                .build();
    }
    public Authentication getAuthentication(String token){
        Claims claims = parse(token);
        if(claims.get(AUTHORITIES_KEY) == null){
            throw new RuntimeException("No Auth Token");
        }
        Collection<? extends GrantedAuthority> authorities =
                Arrays.stream(claims.get(AUTHORITIES_KEY).toString().split(","))
                        .map(SimpleGrantedAuthority::new)
                        .toList();
        UserDetails principal = new User(claims.getSubject(), "", authorities);
        return new UsernamePasswordAuthenticationToken(principal,"",authorities);
    }
    public boolean validateToken(String token){
        try {
            Jwts.parser().verifyWith(secretKey).build().parseSignedClaims(token);
            return true;
        }catch (io.jsonwebtoken.security.SecurityException | MalformedJwtException e){
            log.info("Invalid JWT Token");
        }catch (ExpiredJwtException e){
            log.info("만료된 토큰");
            throw new CustomException(ControllerMessage.EXPIRED_ACCESS_TOKEN);
        }catch (UnsupportedJwtException e){
            log.info("Unsupported JWT Token");
        }catch (IllegalArgumentException e){
            log.info("JWT claims string is empty");
        }
        return false;
    }
    public Long getParseId(String token){
        return Long.valueOf(parse(token).getSubject());
    }
    private Claims parse(String token){
        try{
            return Jwts.parser()
                    .verifyWith(secretKey)
                    .build().parseSignedClaims(token).getPayload();
        }catch (ExpiredJwtException e){
            return e.getClaims();
        }
    }
    public String resolveAccessToken(HttpServletRequest request){
        if(!StringUtils.hasText(AUTHORIZATION_HEADER)){
            return null;
        }
        String bearerToken = request.getHeader(AUTHORIZATION_HEADER);
//        log.info("bearerToken : {}", bearerToken);
        if(bearerToken != null && bearerToken.startsWith(BEARER_PREFIX)){
            return bearerToken.substring(7);
        }
        return null;
    }
    public void expireToken(String accessToken){
        if(!validateToken(accessToken)){
            throw new CustomException(ControllerMessage.EXPIRED_ACCESS_TOKEN);
        }
        Claims claims = parse(accessToken);
        refreshTokenRepository.deleteByMemberId(Long.valueOf(claims.getSubject()));
        Date expiration = claims.getExpiration();
        Date now = new Date();
        Long remains = expiration.getTime() - now.getTime();
//        log.info("accessToken remains : {}",remains);
        redisService.blacklistToken(accessToken,remains);
    }

    private void updateRefreshToken(Long memberId, String refreshToken){
        Member member = memberRepository.findById(memberId).orElseThrow(() -> new CustomException(ControllerMessage.INVALID_MEMBER));
        member.updateRefreshToken(refreshToken);
        memberRepository.save(member);
    }
}
