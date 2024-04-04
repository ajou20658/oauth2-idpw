package com.login.login.service.idpwlogin;

import com.login.login.member.entity.Member;
import com.login.login.member.entity.MemberRepository;
import com.login.login.service.idpwlogin.dto.CustomUserDetails;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
@Slf4j
public class CustomIdPwLoginService implements UserDetailsService {
    private final MemberRepository memberRepository;
    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        Optional<Member> optionalMember = memberRepository.findByEmail(email);
        Member member = optionalMember.orElseThrow(() -> new UsernameNotFoundException("등록되지 않은 ID입니다."));
        return CustomUserDetails.builder()
                .id(member.getId())
                .email(member.getEmail())
                .name(member.getName())
                .authority(List.of(new SimpleGrantedAuthority(member.roleKey())))
                .password(member.getPassword())
                .build();
    }

}
