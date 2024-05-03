package com.login.login.infrastructure.entity.jwt;

import com.login.login.infrastructure.entity.member.Member;
import jakarta.persistence.*;
import lombok.Getter;

@Entity
@Getter
public class RefreshToken {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @OneToOne
    @JoinColumn(name = "member_id", referencedColumnName = "id")
    private Member member;

    private String refreshToken;
}
