package org.zerock.mreview.entity;

import lombok.*;

import javax.persistence.*;

@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@ToString(exclude = "movie") // 연관 관계 주의, @ToString은 항상 exclude, exclude: 해당 속성값으로 지정된 변수는 toString()에서 제외하기 떄문에 지연 로딩을 할 때는 반드시 지정
public class MovieImage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long inum; // 이미지 번호
    private String uuid; // 고유 식별 번호
    private String imgName; // 이미지 이름
    private String path; // 저장 경로, 년/월/일 폴더 구조 의미

    @ManyToOne(fetch = FetchType.LAZY) // 데이터베이스상에서 외래키(FK)의 관계로 연결된 엔티티 클래스에 설정, fetch는 지연 로딩
    private Movie movie;
}
