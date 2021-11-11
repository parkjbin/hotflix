package org.zerock.mreview.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.zerock.mreview.entity.Movie;
import org.zerock.mreview.repository.search.SearchMovieRepository;

import java.util.List;

public interface MovieRepository extends JpaRepository<Movie, Long>, SearchMovieRepository {
    // coalesce(): 첫 번째 인자가 Null이 아닌 경우 첫번째 인자 반환, 아닐 경우 두번째 인자 반환
    // distinct: 중복 값 제거
//    @Query("select m, avg(coalesce(r.grade, 0)), count(distinct r) from Movie m " + "left outer join Review r on r.movie = m group by m")

    // 각 영화마다 이미지를 찾는 쿼리가 실행되면서 비효율적으로 여러 번 실행
//    @Query("select m, max(mi), avg(coalesce(r.grade, 0)), count(distinct r) from Movie m " +
//            "left outer join MovieImage mi on mi.movie = m " +
//            "left outer join Review r on r.movie = m group by m")

    @Query("select m, mi, avg(coalesce(r.grade, 0)), count(r) from Movie m " +
            "left outer join MovieImage mi on mi.movie = m " +
            "left outer join Review r on r.movie = m group by m")
    Page<Object[]> getListPage(Pageable pageable); // 영화 목록 페이지 처리

//    @Query("select m, mi " +
//            "from Movie m left outer join MovieImage mi on mi.movie = m " +
//            "where m.mno = :mno")

    @Query("select m, mi, avg (coalesce(r.grade,0)), count(r) from Movie m " +
            "left outer join MovieImage mi on mi.movie = m " +
            "left outer join Review r on r.movie = m " +
            "where m.mno = :mno group by mi")
    List<Object[]> getMovieWithAll(@Param("mno") Long mno); // 특정 영화 조회

}
