package org.zerock.mreview.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.zerock.mreview.entity.MovieImage;

public interface MovieImageRepository extends JpaRepository<MovieImage, Long> {
    @Modifying
    @Query("delete from MovieImage mi where mi.movie.mno = :mno") // 등록된 영화 삭제 전 영화 이미지들 삭제
    void deleteByImages(@Param("mno") Long mno);
}
