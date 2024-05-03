package com.login.login;

import com.login.login.common.exception.CustomException;
import com.login.login.infrastructure.entity.member.Member;
import com.login.login.infrastructure.entity.member.MemberRepository;
import com.login.login.infrastructure.entity.member.Role;
import com.login.login.infrastructure.persistent.rdbms.RDBMSMemberService;
import com.login.login.domain.model.idpw.SignupRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class MemberServiceTest {

    @Mock
    private MemberRepository memberRepository;
    @Mock
    private PasswordEncoder passwordEncoder;
    private RDBMSMemberService memberService;
    @BeforeEach
    public void setUp(){
        MockitoAnnotations.openMocks(this);
        memberService = new RDBMSMemberService(memberRepository,passwordEncoder);
    }
    @Test
    @DisplayName("회원가입 테스트")
    public void saveMemberTest() {
        // given
        SignupRequest signupRequest = SignupRequest.builder()
                .email("test@example.com")
                .password("password123")
                .role_key("ROLE_USER")
                .build();
        Member savedMember = Member.builder()
                .email("test@example.com")
                .password(passwordEncoder.encode("password123"))
                .role(Role.findByKey("ROLE_USER"))
                .build();

        when(memberRepository.findByEmail(any())).thenReturn(Optional.empty());
        when(memberRepository.save(any())).thenReturn(savedMember);

        // when
        Member testMember = memberService.saveMember(signupRequest);

        // then
        assertEquals(signupRequest.getEmail(), testMember.getEmail());
        assertEquals(signupRequest.getRole_key(), testMember.getRole().key());
        verify(memberRepository, times(1)).findByEmail(any());
        verify(memberRepository, times(1)).save(any());
    }

    @Test
    @DisplayName("이미 존재하는 이메일로 회원가입 시도")
    public void saveMemberWithExistingEmailTest() {
        // given
        SignupRequest signupRequest = SignupRequest.builder()
                .email("existing@example.com")
                .password("password123")
                .role_key("ROLE_USER")
                .build();
        Member existingMember = Member.builder()
                .email("existing@example.com")
                .password("password123")
                .role(Role.findByKey("ROLE_USER"))
                .build();

        when(memberRepository.findByEmail(any())).thenReturn(Optional.of(existingMember));

        // when, then
        assertThrows(CustomException.class, () -> memberService.saveMember(signupRequest));
        verify(memberRepository, times(1)).findByEmail(any());
        verify(memberRepository, never()).save(any());
    }

}
