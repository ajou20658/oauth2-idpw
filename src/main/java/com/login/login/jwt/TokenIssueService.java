package com.login.login.jwt;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TokenIssueService {
    private final JwtTokenProvider jwtTokenProvider;

}
