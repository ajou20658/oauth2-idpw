package com.login.login.domain.model.response;

import com.login.login.infrastructure.entity.member.Role;
import lombok.*;

@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
@AllArgsConstructor
public class MemberDto {
    private String name;
    private String profile;
    private String email;
    private String role;
}
