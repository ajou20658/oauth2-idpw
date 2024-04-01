package com.login.login.service.oauth2.success;

import com.login.login.member.Member;
import com.login.login.member.MemberRepository;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.minidev.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
@Slf4j
public class CustomLoginSuccessHandler implements AuthenticationSuccessHandler {
    private final RestTemplate restTemplate;
    @Value("${jwt.url}")
    private String jwtUrl;
    @Value("${jwt.issue}")
    private String issue;
    private final MemberRepository memberRepository;
    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {

        OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();
        Map<String,Object> attributes = oAuth2User.getAttributes();
        String authorities =authentication.getAuthorities().stream().map(GrantedAuthority::getAuthority).collect(Collectors.joining(","));
        String jwtGenerateUrl = jwtUrl+issue+"?userId="+attributes.get("id")+"&userName="+attributes.get("name")+"&authorities="+authorities;
//        Map<String,Object> params = new HashMap<>();
//        params.put("userId",attributes.get("id"));
//        params.put("userName", attributes.get("name"));
//        params.put("authorities",);
        Map<String,Object> jwt = restTemplate.getForObject(jwtGenerateUrl, JSONObject.class);
        log.info(jwt.toString());
        String access = jwt.get("accessToken").toString();
        String refresh = jwt.get("refreshToken").toString();
        Member member = memberRepository.findById(Long.valueOf(attributes.get("id").toString())).get();
        member.updateRefreshToken(refresh);
        memberRepository.save(member);
        request.getSession().setAttribute("access",access);
        request.getSession().setAttribute("refresh", refresh);
        response.sendRedirect(request.getContextPath()+"/api/jwt");
    }
}
