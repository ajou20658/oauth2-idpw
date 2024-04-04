package com.login.login.service.idpwlogin;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;

@Slf4j
@RequiredArgsConstructor
public class CustomAuthenticationProvider implements AuthenticationProvider {
    private final CustomIdPwLoginService customIdPwLoginService;
    private final PasswordEncoder passwordEncoder;
    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        String username = authentication.getName();
        log.info("authentication전체 : {}",authentication);
        log.info("authenticationProvider에서 username: {}",username);
        String password = authentication.getCredentials().toString();
        log.info("authenticationProvider에서 password: {}",password);
        UserDetails userDetails;
        try{
            userDetails = customIdPwLoginService.loadUserByUsername(username);
        }catch (UsernameNotFoundException ex){
            throw new UsernameNotFoundException("사용자를 찾을 수 없습니다.");
        }
        //!password.equals(userDetails.getPassword())
        if(!passwordEncoder.matches(password,userDetails.getPassword())){
            throw new BadCredentialsException("잘못된 비밀번호입니다.");
        }
        return new UsernamePasswordAuthenticationToken(userDetails,null,userDetails.getAuthorities());
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return authentication.equals(UsernamePasswordAuthenticationToken.class);
    }
}
