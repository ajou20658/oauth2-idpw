package com.login.login.domain.service.idpw;

import com.login.login.infrastructure.entity.member.Member;
import com.login.login.infrastructure.entity.member.MemberRepository;
import com.login.login.domain.model.idpw.CustomUserDetails;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

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
