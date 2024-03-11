package com.login.login;

import com.login.login.exception.ControllerMessage;
import com.login.login.exception.CustomException;
import com.login.login.jwt.JwtTokenProvider;
import com.login.login.jwt.TokenDto;
import com.login.login.oauth2.service.dto.ApiResponse;
import jakarta.annotation.Nullable;
import jakarta.servlet.http.HttpServletRequest;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/")
@RequiredArgsConstructor
public class Controller {
    private final JwtTokenProvider jwtTokenProvider;

    @Deprecated(since = "테스트용")
    @GetMapping("/api/user")
    public OAuth2User getCurrentUser(){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof OAuth2User) {
            return (OAuth2User) authentication.getPrincipal();
        }else{
            throw new CustomException(ControllerMessage.PLEASE_LOGIN);
        }
    }
    @GetMapping("/")
    public ApiResponse jwtIssue(@Nullable Authentication authentication){
        if(authentication!=null){
            TokenDto tokenDto = jwtTokenProvider.jwtIssue(authentication);
            if(tokenDto!=null)
                return new ApiResponse(HttpStatus.OK, tokenDto);
        }
        throw new CustomException(ControllerMessage.RE_LOGIN);
    }
    @GetMapping("/api/token")
    public ApiResponse jwtViewer(Authentication authentication){
        TokenDto tokenDto = jwtTokenProvider.jwtIssue(authentication);
        return new ApiResponse(HttpStatus.OK, tokenDto);
    }
    @GetMapping("/api/reissue")
    public ApiResponse reissue(HttpServletRequest request) {//헤더로 요청하게 설정
        TokenDto tokenDto = jwtTokenProvider.jwtRefresh(jwtTokenProvider.resolveAccessToken(request));
        return new ApiResponse(HttpStatus.OK,tokenDto);
    }
    @GetMapping("/api/logout")
    public ApiResponse logout(HttpServletRequest request){ //헤더로 요청하게 설정
        jwtTokenProvider.logout(jwtTokenProvider.resolveAccessToken(request));
        return new ApiResponse(HttpStatus.OK, "로그아웃 성공");
    }
}
