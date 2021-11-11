package org.zerock.mreview.security.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.extern.log4j.Log4j2;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.util.Collection;
import java.util.Map;

@Log4j2
@Getter
@Setter
@ToString
public class ClubAuthMemberDTO extends User implements OAuth2User {
    private String email;
    private String password;
    private String name;
    private boolean fromSocial;
    private Map<String, Object> attr;

    public ClubAuthMemberDTO (String username, String password, boolean fromSocial, Collection<? extends GrantedAuthority> authorities, Map<String, Object> attr) {
        this(username, password, fromSocial, authorities);
        this.attr = attr;
    }


    // <? extends T>: 상속 관계로 이루어진 클래스만 자료형을 받는다.
    // ?(자식 클래스), T(부모클래스)로 부모 클래스와 자식 클래스의 임의의 자료형만 받는다. 즉, 매개변수의 자료형을 특정 클래스를 상속받은 클래스로만 제한함
    public ClubAuthMemberDTO(String username, String password, boolean fromSocial, Collection<? extends GrantedAuthority> authorities) {
        super(username, password, authorities);
        this.email = username;
        this.password = password;
        this.fromSocial = fromSocial;
    }

    @Override
    public Map<String, Object> getAttributes() {
        return this.attr;
    }
}
