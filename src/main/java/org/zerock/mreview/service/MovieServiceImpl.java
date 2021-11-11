package org.zerock.mreview.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.zerock.mreview.dto.MovieDTO;
import org.zerock.mreview.dto.PageRequestDTO;
import org.zerock.mreview.dto.PageResultDTO;
import org.zerock.mreview.entity.Movie;
import org.zerock.mreview.entity.MovieImage;
import org.zerock.mreview.entity.Review;
import org.zerock.mreview.repository.MovieImageRepository;
import org.zerock.mreview.repository.MovieRepository;
import org.zerock.mreview.repository.ReviewRepository;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

@Service
@Log4j2
@RequiredArgsConstructor
public class MovieServiceImpl implements MovieService {

    private final MovieRepository movieRepository;
    private final MovieImageRepository imageRepository;
    private final ReviewRepository reviewRepository;

    @Transactional
    @Override
    public Long register(MovieDTO movieDTO) {

        Map<String, Object> entityMap = dtoToEntity(movieDTO); // Entity 타입으로 변환 된 영화 객체와 영화 리스트 객체를 가져옴
        Movie movie = (Movie)entityMap.get("movie"); // 영화 객체를 가져옴
        List<MovieImage> movieImageList = (List<MovieImage>) entityMap.get("imgList"); // List<>에 담겨있는 영화 이미지 객체를 가져옴

        // 영화 객체를 DB에 저장
        movieRepository.save(movie);
        // List<>에 담겨 있는 영화 이미지들의 객체를 DB에 저장
        movieImageList.forEach(movieImage -> {
            imageRepository.save(movieImage);
        });

        // 영화 객체 안에 있는 영화 번호를 반환
        return movie.getMno();
    }

    @Override
    public PageResultDTO<MovieDTO, Object[]> getList(PageRequestDTO requestDTO) { // 영화 목록 리스트
        log.info("getList(): " + requestDTO);

//        Pageable pageable = requestDTO.getPageable(Sort.by("mno").descending());
//        Page<Object[]> result = movieRepository.getListPage(pageable);

        Page<Object[]> result = movieRepository.searchPage(
          requestDTO.getType(),
          requestDTO.getKeyword(),
          requestDTO.getPageable(Sort.by("mno").descending()) );

        // Object[]: 영화, 영화 이미지 리스트, 평점 평균, 리뷰 개수의 객체들을 DTO 타입으로 변환
        // asList(): 일반 배열을 arrayList로 변환
        Function<Object[], MovieDTO> fn = (arr -> entitiesToDTO(
                (Movie)arr[0],
                (List<MovieImage>)(Arrays.asList((MovieImage)arr[1])),
                (Double)arr[2],
                (Long)arr[3])
        );
        log.info(fn);
        
        // result: 페이징 및 정렬 처리, fn: 엔티티 객체들을 DTO 타입으로 변환 된 객체
        return new PageResultDTO<>(result, fn);
    }

    @Override
    public MovieDTO getMovie(Long mno) { // 영화 상세 조회
        List<Object[]> result = movieRepository.getMovieWithAll(mno);

        Movie movie = (Movie)result.get(0)[0]; // Movie 엔티티는 가장 앞에 존재 - 모든 Row가 동일한 값
        
        List<MovieImage> movieImageList = new ArrayList<>(); // 영화 이미지 개수만큼 MovieImage 객체 필요

        result.forEach(arr -> {
            MovieImage movieImage = (MovieImage)arr[1];
            movieImageList.add(movieImage);
        });

        Double avg = (Double)result.get(0)[2]; // 평균 평점 - 모든 Row가 동일한 값
        Long reviewCnt = (Long)result.get(0)[3]; // 리뷰 개수 - 모든 Row가 동일한 값

        return entitiesToDTO(movie, movieImageList, avg, reviewCnt);
    }

    @Transactional
    @Override
    public void removeWithReplise(Long mno) { // 등록된 영화 및 리뷰 삭제
        log.info("mno: " + mno);

        // 댓글 먼저 삭제
        reviewRepository.deleteByReviews(mno);
        // 영화 이미지들 삭제
        imageRepository.deleteByImages(mno);
        // 이후 영화 삭제
        movieRepository.deleteById(mno);
    }

    @Transactional // 해당 메서드를 하나의 트랜잭션으로 처리하라는 의미, no Session 발생 시 데이터베이스와 연결 생성
    @Override
    public void modify(MovieDTO movieDTO) { // 영화 제목 수정 기능
        // JPA를 이용하여 파라미터로 넘오온 MovieDTO 객체 안에 있는 mno를 가진 데이터를 Movie Entity 클래스 변수에 저장
        log.info(movieDTO.getMno());
        Movie movie = movieRepository.getOne(movieDTO.getMno());

        // 파라미터로 넘어온 DTO 타입의 객체 안에 있는 제목 변경
        movie.changeTitle(movieDTO.getTitle());
        movie.changeSponsor(movieDTO.getSponsor());

        // JPA를 이용하여 Board 객체를 데이터베이스에 수정(update문) 결과를 저장
        movieRepository.save(movie);
    }
}
