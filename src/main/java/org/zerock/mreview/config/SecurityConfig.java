package org.zerock.mreview.config;

import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.zerock.mreview.security.handler.ClubLoginSuccessHandler;
import org.zerock.mreview.security.service.ClubUserDetailsService;

// WebSecurityConfigurerAdapter: override를 통해서 여러 설정을 조정, 시큐리티 관련 기능을 쉽게 설정하는 용도로 사용
@Configuration
@Log4j2
@EnableGlobalMethodSecurity(prePostEnabled = true, securedEnabled = true) // 어노테이션 기반의 접근 제한을 설정할 수 있도록 하는 설정
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    @Autowired
    private ClubUserDetailsService userDetailsService;

//  Bean이란 : Spring IoC 컨테이너가 관리하는 자바 객체를 빈(Bean)이라고 한다.
//  @Component : 개발자가 직접 작성한 Class를 Bean으로 만드는 것
//  @Bean : 개발자가 작성한 Method를 통해 반환되는 객체를 Bean으로 만드는 것 
    @Bean
    // PasswordEncoder: 비밀번호를 안전하게 저장할 수 있도록 비밀번호의 단방향 암호화를 지원
    PasswordEncoder passwordEncoder() {
        // BCryptPasswordEncoder(): BCrypt 해싱 함수(BCrypt hashing function)를 사용해서 비밀번호를 인코딩해주는 메서드와 사용자의 의해 제출된 비밀번호와 저장소에 저장되어 있는 비밀번호의 일치 여부를 확인
        return new BCryptPasswordEncoder();
    }

    // AuthenticationManagerBuilder: 인증에 대한 지원을 설정하는 몇 가지 메소드를 제공, 자세한 내용은 study.txt 링크 참조
//    @Override
//    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
//        // 사용자 계정은 user1
//        // inMemoryAuthentication(): 인메모리 사용자 저장소가 활성화
//        // withUser(): UserDetailsManagerConfigurer.UserDetailBuilder를 반환하고, 이는 사용자 암호를 설정하는 password()와 사용자에게 권한에 대한 역할을 부여해 주는 roles()를 포함한 몇 가지 사용자 설정 메소드를 제공
//        // password(): 사용자의 암호를 명시
//        // roles(): 사용자에게 부여된 역할을 명시
//        auth.inMemoryAuthentication().withUser("user1")
//        // 1111 패스워드 인코딩 결과(PasswordTests.class에 있는 testEncode()를 호출해서 나온 결과)
//                .password("$2a$10$g/XjnY2XAd70itbHgKyKGeV2vZFwK.ob4FqO4CL70sxYHRFNhmRpi")
//                .roles("USER");
//    }

    // HttpSecurity: 몇 가지 HTTP 보안의 관점을 설정하기 위해 사용(접근 제한 처리)
    // 1) 사용자 인증이 된 요청에 대해서만 요청을 허용, 2) 사용자는 폼기반 로그인으로 인증, 3) 사용자는 HTTP기반 인증으로 인증
    @Override
    protected void configure(HttpSecurity http) throws Exception {
        // authorizeRequests(): 인증이 필요한 자원들을 설정, 접근 제한을 담당
        // antMatchers(): 패턴으로 원하는 자원을 선택
        // permitAll(): 모든 사용자에게 허락한다는 의미
        // hasRole(): "USER" 권한이 있는 사용자만 사용
        // formLogin(): 인가, 인증 절차에서 문제가 발생했을 때 로그인 페이지를 보여주도록 지정
        // CSRF(Cross Site Request Forgery): 크로스 사이트 요청 위조 방식의 공격 방법
        // csrf().disable(): CSRF 토큰을 발생하지 않도록 설정
        // logout() : 별도의 설정이 없는 경우 로그 아웃 처리, CSRF 토큰을 사용 할 경우 반드시 POST 방식으로 로그 아웃 처리, CSRF 토큰 disable() 시 GET 방식으로 로그 아웃 처리
//        http.authorizeRequests()
//                .antMatchers("/freeboard/list").permitAll()
//                .antMatchers("/freeboard/modify").hasRole("ADMIN")
//                .antMatchers("/freeboard/read").hasRole("USER")
//                .antMatchers("/freeboard/register").hasRole("ADMIN");
//        .antMatchers("/", "/signup");

        http.formLogin();
        http.csrf().disable();
        http.logout()
                .logoutSuccessUrl("/");

        http.oauth2Login().successHandler(successHandler());
        // rememberMe(): 사용자의 로그인 정보를 쿠키에 7일간 저장하는 체크 박스 생성, 체크하고 로그인하면 7일간 쿠키가 저장됨
        // 7일간 로그인 없이 사이트에 접근 가능, 단 소셜 로그인(Google 계정)을 이용하면 쿠기 생성 안됨
        http.rememberMe().tokenValiditySeconds(60*60*24*7).userDetailsService(userDetailsService); // 7일
    }

    @Bean
    public ClubLoginSuccessHandler successHandler() {
        return new ClubLoginSuccessHandler(passwordEncoder());
    }
}
