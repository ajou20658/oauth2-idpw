package com.login.login.jwt;

import com.login.login.domain.member.Member;
import com.login.login.domain.member.MemberRepository;
import com.login.login.exception.ControllerMessage;
import com.login.login.exception.CustomException;
import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.crypto.SecretKey;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Service
@Slf4j
public class JwtTokenProvider {
    @Value("${jwt.issuer}")
    private String issuer;
    @Value("${jwt.expiration}")
    private Long expiration;
    @Value("${JWT_REFRESH}")
    private Long refresh;
    private final RedisTemplate<String,Object> redisTemplate;
    private final MemberRepository memberRepository;
    public static final String AUTHORIZATION_HEADER = "Authorization";
    public static final String BEARER_PREFIX = "Bearer ";
    public static final String AUTHORITIES_KEY="auth";
    public static final String USER_ID = "UID";
    public static final String USER_IMAIL = "UIM";
    public static final String USER_PROFILE = "UP";
    private final SecretKey secretKey;
    public JwtTokenProvider(@Value("${jwt.secret}") String secretKey,MemberRepository memberRepository,RedisTemplate<String,Object> redisTemplate){
        this.secretKey = Keys.hmacShaKeyFor(Decoders.BASE64URL.decode(secretKey));
        this.memberRepository = memberRepository;
        this.redisTemplate = redisTemplate;
    }
    public TokenDto generateToken(Authentication authentication){
        String authorities = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.joining(","));
        long now = (new Date()).getTime();
        OAuth2User oAuth2User = (OAuth2User)authentication.getPrincipal();
        Long userId = oAuth2User.getAttribute("id");
        Date accessTokenExpiresIn = new Date(now + expiration);
        Claims claims = Jwts.claims().subject(authentication.getName())
                .add(AUTHORITIES_KEY,authorities)
                .add(USER_ID, userId).build();
        String refreshToken = Jwts.builder().expiration(new Date(now + refresh))
                .signWith(secretKey, Jwts.SIG.HS512)
                .claims(claims)
                .compact();
        String accessToken = Jwts.builder()
                .expiration(new Date(now + expiration))
                .claims(claims)
                .signWith(secretKey, Jwts.SIG.HS512)
                .compact();
        return TokenDto.builder()
                .grantType(BEARER_PREFIX)
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .accessTokenExpiresIn(accessTokenExpiresIn.getTime())
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
            log.info("Invalid JWT Token", e);
        }catch (ExpiredJwtException e){
            throw new CustomException(ControllerMessage.EXPIRED_ACCESSTOKEN);
        }catch (UnsupportedJwtException e){
            log.info("Unsupported JWT Token", e);
        }catch (IllegalArgumentException e){
            log.info("JWT claims string is empty", e);
        }
        return false;
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
    @Transactional
    public TokenDto jwtIssue(Authentication authentication){
        if(authentication!=null){
            TokenDto tokenDto = generateToken(authentication);
            Optional<Member> optionalMember = memberRepository.findById(getMemberIdFromToken(tokenDto.getAccessToken()));
            if(optionalMember.isPresent()){
                Member member = optionalMember.get();
                member.updateRefreshToken(tokenDto.getRefreshToken());
                memberRepository.save(member);
                return tokenDto;
            }
        }
        throw new CustomException(ControllerMessage.RE_LOGIN);
    }
//    public Long getUserIdFromToken(String token){
//        Claims claims = parse(token);
//        return Long.parseLong(claims.getSubject()); //유저 아이디 반환
//    }
    private long getMemberIdFromToken(String token){
        return (int) parse(token).get(USER_ID);
    }
    @Transactional
    public TokenDto jwtRefresh(String refreshToken){
        TokenDto tokenDto;
        Optional<Member> memberOptional = memberRepository.findByRefreshToken(refreshToken);
        if(memberOptional.isEmpty()){
            throw new CustomException(ControllerMessage.INVALID_TOKEN);
        }
        Member member = memberOptional.get();
        if(!member.getRefreshToken().equals(refreshToken)){ //저장된 토큰과 동일하지 않으면
            throw new CustomException(ControllerMessage.INVALID_TOKEN);
        }
        if(!validateToken(refreshToken)){ //만료되었으면
            throw new CustomException(ControllerMessage.EXPIRED_ACCESSTOKEN);
        }

        tokenDto = generateToken(getAuthentication(refreshToken));
        member.updateRefreshToken(tokenDto.getRefreshToken());
//            member.updateRefreshToken(null);
        memberRepository.save(member);

        return tokenDto;
    }
    public Long getExpiration(String token){
        return parse(token).getExpiration().getTime();
    }
    public String resolveAccessToken(HttpServletRequest request){
        String bearerToken = request.getHeader(AUTHORIZATION_HEADER);
        log.info("bearerToken : {}", bearerToken);
        if(bearerToken != null && bearerToken.startsWith(BEARER_PREFIX)){
            return bearerToken.substring(7);
        }
        return null;
    }
    public void logout(String token){
        Long memberId = (long) getMemberIdFromToken(token);
        Optional<Member> optionalMember = memberRepository.findById(memberId);
        if(optionalMember.isEmpty()){
            throw new CustomException(ControllerMessage.INVALID_TOKEN);
        }
        Member member = optionalMember.get();
        member.updateRefreshToken(null);//리프레시 토큰 제거
        Long expiration = getExpiration(token);
        redisTemplate.opsForValue().set(token,"logout", expiration, TimeUnit.MILLISECONDS);

    }
}
