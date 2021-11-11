package org.zerock.mreview.service;

import org.springframework.data.domain.Pageable;
import org.zerock.mreview.dto.MovieDTO;
import org.zerock.mreview.dto.MovieImageDTO;
import org.zerock.mreview.dto.PageRequestDTO;
import org.zerock.mreview.dto.PageResultDTO;
import org.zerock.mreview.entity.Movie;
import org.zerock.mreview.entity.MovieImage;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public interface MovieService {
    Long register(MovieDTO movieDTO); // 영화 등록
    PageResultDTO<MovieDTO, Object[]> getList(PageRequestDTO requestDTO); // 목록 처리
    MovieDTO getMovie(Long mno); // 영화 조회 처리
    void removeWithReplise(Long mno); // 영화 및 리뷰 삭제
    void modify(MovieDTO movieDTO); // 영화 제목 수정
    
    // Map<>: Movie 객체와 MovieImage 객체 2개의 객체를 Object 타입으로 Map 컬렉션에 담아서 반환
    default Map<String, Object> dtoToEntity(MovieDTO movieDTO) { // map 컬렉션 타입으로 반환
        Map<String, Object> entityMap = new HashMap<>();

        // MovieDTO 객체를 Movie Entity 타입으로 변환
        Movie movie = Movie.builder()
                .mno(movieDTO.getMno())
                .title(movieDTO.getTitle())
                .sponsor(movieDTO.getSponsor())
                .build();

        entityMap.put("movie", movie); // Map 컬렉션에 추가

        List<MovieImageDTO> imageDTOList = movieDTO.getImageDTOList();

        // MovieImageDTO 처리
        // 즉, List에 영화가 없지 않거나 List의 사이즈가 0보다 크다면, 즉 List 영화 이미지가 있다면~
        if(imageDTOList != null && imageDTOList.size() > 0) {
            // MovieImageDTO를 MovieImage Entity 타입으로 변환하고 List 컬렉션에 저장
            List<MovieImage> movieImageList = imageDTOList.stream().map(movieImageDTO -> {
                MovieImage movieImage = MovieImage.builder()
                        .path(movieImageDTO.getPath())
                        .imgName(movieImageDTO.getImgName())
                        .uuid(movieImageDTO.getUuid())
                        .movie(movie)
                        .build();
                return movieImage;
            }).collect(Collectors.toList());
            
            // Map 컬렉션에 이미지 List 추가
            entityMap.put("imgList", movieImageList);
        }
        // 반환 결과, entityMap: Map<(String: "movie", "imgList"), Object: (movie 객체와 movieImageList 객체(List 담겨 있음))>
        return entityMap;
    }

    // 파라미터: 영화, 영화 이미지 리스트, 평점 평균, 리뷰 개수
    default MovieDTO entitiesToDTO(Movie movie, List<MovieImage> movieImages, Double avg, Long reviewCnt) {
        // Movie Entity -> MovieDTO, 영화 객체
        MovieDTO movieDTO = MovieDTO.builder()
                .mno(movie.getMno())
                .title(movie.getTitle())
                .sponsor(movie.getSponsor())
                .regDate(movie.getRegDate())
                .modDate(movie.getModDate())
                .build();

        // MovieImage Entity -> MovieImageDTO, 영화 이미지들(List)
        List<MovieImageDTO> movieImageDTOList = movieImages.stream().map(movieImage -> {
            return MovieImageDTO.builder()
                    .imgName(movieImage.getImgName())
                    .path(movieImage.getPath())
                    .uuid(movieImage.getUuid())
                    .build();
        }).collect(Collectors.toList());
        
        movieDTO.setImageDTOList(movieImageDTOList); // 영화 이미지들의 객체를 MovieDTO 안에 있는 imageDTOList에 저장
        if(avg == null) avg = 0.0;
        if(reviewCnt == null) reviewCnt = 0L;
        movieDTO.setAvg(avg); // 영화 평점 평균을 MovieDTO 안에 있는 avg에 저장
        movieDTO.setReviewCnt(reviewCnt.intValue()); // 영화 리뷰 개수를 MovieDTO 안에 있는 reviewCnt에 저장

        return movieDTO;
    }
}
