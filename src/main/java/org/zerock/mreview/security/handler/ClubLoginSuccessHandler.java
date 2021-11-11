package org.zerock.mreview.security.handler;

import lombok.extern.log4j.Log4j2;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.DefaultRedirectStrategy;
import org.springframework.security.web.RedirectStrategy;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.zerock.mreview.security.dto.ClubAuthMemberDTO;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Log4j2
public class ClubLoginSuccessHandler implements AuthenticationSuccessHandler {
    // RedirectStrategy: 스프링 시큐리티가 화면 이동에 대한 규칙을 정의하는 부분을 만든 인터페이스, 이 인터페이스를 구현한 클래스로 redirect를 하면 된다.
    // DefaultRedirectStrategy(): RedirectStrategy 인터페이스를 구현한 구현 클래스의 객체 생성
    private RedirectStrategy redirectStrategy = new DefaultRedirectStrategy();

    // PasswordEncoder: Spring Security에서는 비밀번호를 안전하게 저장할 수 있도록 비밀번호의 단방향 암호화를 지원
    private PasswordEncoder passwordEncoder;

    public ClubLoginSuccessHandler(PasswordEncoder passwordEncoder) {
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        log.info("----------------------------------------");
        log.info("onAuthenticationSuccess ");

        // getPrincipal(): UserDetails를 구현한 사용자 객체의 정보들을 가져온다.
        ClubAuthMemberDTO authMember = (ClubAuthMemberDTO)authentication.getPrincipal();

        boolean fromSocial = authMember.isFromSocial();

        log.info("Need Modify Member?" + fromSocial);

        boolean passwordResult = passwordEncoder.matches("1111", authMember.getPassword());

        if(fromSocial && passwordResult) {
            redirectStrategy.sendRedirect(request, response, "/member/modify?from=social");
        }
    }
}
