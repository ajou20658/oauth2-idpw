package com.login.login.infrastructure.entity.jwt;

import org.springframework.data.jpa.repository.JpaRepository;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken,Long> {
    RefreshToken findByMemberId(Long memberId);
    void deleteByMemberId(Long memberId);
}
