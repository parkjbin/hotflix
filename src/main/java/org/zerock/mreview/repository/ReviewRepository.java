package org.zerock.mreview.repository;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.zerock.mreview.entity.Member;
import org.zerock.mreview.entity.Movie;
import org.zerock.mreview.entity.Review;

import java.util.List;

public interface ReviewRepository extends JpaRepository<Review, Long> {
    
    // LAZY: 지연 로딩, EAGER: 즉시 로딩
    // @EntityGraph: 엔티티의 특정한 속성을 같이 로딩, 특정 기능을 수행할 때만 LAZY -> EAGER 로딩을 하도록 지정
    // attributePaths: 로딩 설정을 변경하고 싶은 속성의 이름을 배열로 명시
    // type: 어떤 방식으로 적용할 것인지를 설정, 여기서는 member 엔티티 객체만 EAGER로 처리 나머지는 LAZY로 처리
    @EntityGraph(attributePaths = {"member"}, type = EntityGraph.EntityGraphType.FETCH)
    List<Review> findByMovie(Movie movie);

    @Modifying // @Query Annotation으로 작성 된 추가,변경,삭제 쿼리 메서드를 사용할 때 필요, 즉, 조회 쿼리를 제외하고, 데이터에 변경이 일어나는 INSERT, UPDATE, DELETE, DDL 에서 사용
    @Query("delete from Review mr where mr.member = :member") // 회원 계정 삭제 전 회원이 작성한 리뷰를 한번에 삭제
    void deleteByMember(Member member);

    @Modifying
    @Query("delete from Review mr where mr.movie.mno = :mno") // 등록된 영화 삭제 전 리뷰 삭제
    void deleteByReviews(@Param("mno") Long mno);
}
