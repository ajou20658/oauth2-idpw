package com.login.login.service.oauth2.dto;

import com.login.login.member.Member;
import com.login.login.member.Role;
import com.login.login.member.Social;
import lombok.Builder;

import java.util.Map;
@Builder
public record OAuthAttributes(
        Map<String,Object> attributes,
        String nameAttributeKey,
        String name,
        String email,
        String profile,
        String registrationId
) {
    public Member toEntity(){
        return Member.builder().name(name).email(email).profile(profile).role(Role.USER)
                .social(Social.valueOf(registrationId.toUpperCase())).build();
    }
}
