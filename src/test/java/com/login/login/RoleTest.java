package com.login.login;

import com.login.login.common.exception.CustomException;
import com.login.login.infrastructure.entity.member.Role;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class RoleTest {
    @Test
    public void testFindByKey(){
        String key = "ROLE_FIRE";
        Role role = Role.findByKey(key);

        assertEquals("ROLE_FIRE",role.key());
    }

    @Test
    public void testFindBYKeyWithInvalidKey(){
        //given
        String invalidKey = "INVALID_ROLE";
        //when


        assertThrows(CustomException.class,() -> {
            Role.findByKey(invalidKey);
        });
    }

}
