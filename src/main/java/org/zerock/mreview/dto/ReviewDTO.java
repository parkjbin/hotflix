package org.zerock.mreview.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReviewDTO {
    private Long reviewnum; // 리뷰 번호
    private Long mno; // 영화 번호
    private Long mid; // 회원 ID
    private String nickname; // 회원 별칭
    private String email; // 회원 이메일
    private int grade; // 평점
    private String text; // 리뷰 내용
    private LocalDateTime regDate, modDate; // 리뷰 등록 시간, 수정 시간
}
