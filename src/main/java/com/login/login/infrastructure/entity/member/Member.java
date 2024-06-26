package com.login.login.infrastructure.entity.member;

import com.login.login.domain.model.idpw.SignupRequest;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;

@Getter
@Entity
@NoArgsConstructor
public class Member extends BaseTime{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private String profile;
    private String email;
    private String password;
    private String refreshToken;
    @Enumerated(EnumType.STRING)
    private Role role;
    @Enumerated(EnumType.STRING)
    private Social social;

    @Builder
    public Member(String name, String profile, String email, String password, Role role, Social social){
        this.name = name;
        this.profile = profile;
        this.email = email;
        this.password = password;
        this.role = role;
        this.social = social;
    }
    public Member update(String name, String profile){
        this.name = name;
        this.profile = profile;
        return this;
    }
    public String roleKey(){
        return role.key();
    }
    public String updateRefreshToken(String refreshToken){
        this.refreshToken = refreshToken;
        return refreshToken;
    }
    public static Member createMember(SignupRequest request, PasswordEncoder passwordEncoder){
        return Member.builder()
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .name(request.getName())
                .role(Role.findByKey(request.getRole_key()))
                .social(Social.IDPW)
                .build();
    }
}
