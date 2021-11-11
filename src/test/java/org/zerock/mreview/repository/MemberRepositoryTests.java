package org.zerock.mreview.repository;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Commit;
import org.springframework.transaction.annotation.Transactional;
import org.zerock.mreview.entity.Member;

import java.util.stream.IntStream;

@SpringBootTest
public class MemberRepositoryTests {

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private ReviewRepository reviewRepository;

    @Test
    public void insertMembers() {
        IntStream.rangeClosed(1, 2).forEach(i -> {
            Member member = Member.builder()
                    .email("r" + i + "@zerock.org")
                    .pw("1111")
                    .nickname("reviewer" + i)
                    .build();

            memberRepository.save(member);
        });
    }

    @Commit
    @Transactional
    @Test
    public void testDeleteMember() {
        Long mid = 3L; // Member의 mid
        Member member = Member.builder().mid(mid).build();

        // 회원 계정 삭제 후 삭제 하려는 회원이 작성한 리뷰 삭제 일때, ConstraintViolationException: could not execute statement
//        memberRepository.deleteById(mid);
//        reviewRepository.deleteByMember(member);

        // 회원 계정 삭제 전 삭제 하려는 회원이 작성한 리뷰를 먼저 삭제 후 회원 계정 삭제
        reviewRepository.deleteByMember(member);
        memberRepository.deleteById(mid);
    }
}
