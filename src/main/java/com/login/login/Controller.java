package com.login.login;

import com.login.login.jwt.JwtTokenProvider;
import com.login.login.jwt.TokenDto;
import com.login.login.oauth2.service.dto.ApiResponse;
import jakarta.annotation.Nullable;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/")
@RequiredArgsConstructor
public class Controller {
    private final JwtTokenProvider jwtTokenProvider;
    @PostMapping("/api/reissue")
    public ResponseEntity<TokenDto> reissue(@RequestHeader(value = "Refresh-Token") String refreshToken) {
        return ResponseEntity.ok().body(null);
    }
    @GetMapping("/api/user")
    public OAuth2User getCurrentUser(){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof OAuth2User) {
            return (OAuth2User) authentication.getPrincipal();
        }else{
            return null;
        }
    }
    @GetMapping("/")
    public ApiResponse hello(@Nullable Authentication authentication){
        if(authentication!=null){
            TokenDto tokenDto = jwtTokenProvider.generateToken(authentication);
            return new ApiResponse(HttpStatus.OK,tokenDto);
        }
        return new ApiResponse(HttpStatus.OK, null);
    }

}
