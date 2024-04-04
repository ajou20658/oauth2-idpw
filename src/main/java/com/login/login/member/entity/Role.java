package com.login.login.member.entity;

import com.login.login.exception.ControllerMessage;
import com.login.login.exception.CustomException;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;

@Getter
@Accessors(fluent = true)
@RequiredArgsConstructor
public enum Role {
    USER("ROLE_USER","일반 사용자"),
    ADMIN("ROLE_ADMIN","관리자"),
    POLICE("ROLE_POLICE", "경찰서"),
    FIRE("ROLE_FIRE","소방서");
    private final String key;
    private final String title;
    public static Role findByKey(String key){

        for(Role role:values()){
            if(role.key.equals(key)){
                return role;
            }
        }
        throw new CustomException(ControllerMessage.BAD_ROLE);
    }
}
