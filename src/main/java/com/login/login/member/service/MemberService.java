package com.login.login.member.service;

import com.login.login.exception.ControllerMessage;
import com.login.login.exception.CustomException;
import com.login.login.member.entity.Member;
import com.login.login.member.entity.MemberRepository;
import com.login.login.member.service.dto.SignupRequest;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class MemberService {
    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;
    /*
    회원가입 로직
     */
    public Member saveMember(SignupRequest request){
        Member member = Member.createMember(request,passwordEncoder);
        validateDupMember(member);
        return memberRepository.save(member);
    }
    private void validateDupMember(Member member){
        Optional<Member> findMember = memberRepository.findByEmail(member.getEmail());
        if(findMember.isPresent()) {
            throw new CustomException(ControllerMessage.DUP_EMAIL);
        }
    }
}
