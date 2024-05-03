package com.login.login.infrastructure.entity.member;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member,Long> {
    Optional<Member> findByEmail(String email);

    boolean existsByEmail(String email);
    boolean existsMemberByIdAndRefreshToken(Long id, String refreshToken);
}
