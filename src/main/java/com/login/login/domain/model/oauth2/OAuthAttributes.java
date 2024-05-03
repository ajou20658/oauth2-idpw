package com.login.login.domain.model.oauth2;

import com.login.login.infrastructure.entity.member.Member;
import com.login.login.infrastructure.entity.member.Role;
import com.login.login.infrastructure.entity.member.Social;
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
