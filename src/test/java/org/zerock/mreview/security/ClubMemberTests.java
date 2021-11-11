package org.zerock.mreview.security;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.zerock.mreview.entity.ClubMember;
import org.zerock.mreview.entity.ClubMemberRole;
import org.zerock.mreview.repository.ClubMemberRepository;

import java.util.Optional;
import java.util.stream.IntStream;

@SpringBootTest
public class ClubMemberTests {
    @Autowired
    private ClubMemberRepository repository;
    @Autowired
    private PasswordEncoder passwordEncoder;

    @Test
    public void insertDummies() {
        // 1 = USER, 2 = MEMBER, 3 = ADMIN
        IntStream.rangeClosed(1,3).forEach(i -> {
            ClubMember clubMember = ClubMember.builder()
                    .email("user" + i + "@movie.com")
                    .name("user" + i)
                    .fromSocial(false)
                    .password(passwordEncoder.encode("1111"))
                    .build();

            // USER 권한
            clubMember.addMemberRole(ClubMemberRole.USER);
            // MANAGER 권한 추가
            if(i > 1) clubMember.addMemberRole(ClubMemberRole.MANAGER);
            // ADMIN 권한 추가
            if(i > 2) clubMember.addMemberRole(ClubMemberRole.ADMIN);

            repository.save(clubMember);
        });
    }

    @Test
    public void testRead() {
        Optional<ClubMember> result = repository.findByEmail("user3@movie.com", false);
        ClubMember clubMember = result.get();
        System.out.println(clubMember);
    }
}
