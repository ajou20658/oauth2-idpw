package com.login.login.member.entity;

import org.springframework.data.domain.Example;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member,Long> {
    Optional<Member> findByEmail(String email);


    boolean existsByEmail(String email);

    Optional<Member> findByRefreshToken(String refreshToken);
}
