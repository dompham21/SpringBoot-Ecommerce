package com.luv2code.user;

import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class PasswordEncodeTest {
    @Test
    public void testEncodePassword() {
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        String psw = "123456";
        String encodePassword = passwordEncoder.encode(psw);
        System.out.println(encodePassword);
        System.out.println(passwordEncoder.matches(psw, encodePassword));
    }
}
