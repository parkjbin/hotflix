package org.zerock.mreview.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MovieDTO {
    private Long mno;
    private String title;
    private String sponsor;

    // 특정 필드/매개변수가 빌드 세션 중에 설정되지 않으면 항상 0/null/false가 됩니다.
    // @Builder.Default: 필드의 기본값을 지정할 때 사용
    // 여기서는 영화 이미지의 데이터를 List로 받기 위함, 즉 영화 1개에 여러개의 영화 이미지를 받기 위함
    @Builder.Default
    private List<MovieImageDTO> imageDTOList = new ArrayList<>();

    // 영화의 평균 평점
    private double avg;

    // 리뷰 수 jpa의 count()
    private int reviewCnt;

    private LocalDateTime regDate;
    private LocalDateTime modDate;
}
