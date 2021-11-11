package org.zerock.mreview.repository;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.zerock.mreview.entity.ClubMember;

import java.util.Optional;

public interface ClubMemberRepository extends JpaRepository<ClubMember, String> {
    // 엔티티들은 서로 연관되어 있는 관계가 보통이며 이 관계는 그래프로 표현이 가능합니다.
    // @EntityGraph는 JPA가 어떤 엔티티를 불러올 때 이 엔티티와 관계된 엔티티를 불러올 것인지에 대한 정보를 제공, 쿼리 메소드 마다 연관 관계의 Fetch 모드를 유연하게 설정, 즉 일반 로그인 사용자와 소셜 로그인 사용자 구별
    @EntityGraph(attributePaths = {"roleSet"}, type = EntityGraph.EntityGraphType.LOAD)
    @Query("select m from ClubMember m where m.fromSocial = :social and m.email = :email")
    // 사용자의 이메일과 소셜로 추가된 회원 여부를 선택해서 동작
    Optional<ClubMember> findByEmail(@Param("email") String email, @Param("social") boolean social);
}
