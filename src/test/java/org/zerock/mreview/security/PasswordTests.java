package org.zerock.mreview.security;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;

@SpringBootTest
public class PasswordTests {

    @Autowired
    private PasswordEncoder passwordEncoder;
    
    @Test
    public void testEncode() { // 패스워드 암호화 테스트
        String password = "1111";
        String enPw = passwordEncoder.encode(password);
        System.out.println("enpw:" + enPw);
        boolean matchResult = passwordEncoder.matches(password, enPw);
        System.out.println("matchResult: " + matchResult);
    }
}
