package org.zerock.mreview.entity;

import lombok.*;

import javax.persistence.*;

@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@ToString(exclude = {"movie", "member"}) // exclude: 해당 속성값으로 지정된 변수는 toString()에서 제외
public class Review extends BaseEntity{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long reviewnum;

    @ManyToOne(fetch = FetchType.LAZY)
    private Movie movie;

    @ManyToOne(fetch = FetchType.LAZY)
    private Member member;

    private int grade;
    private String text;

    public void changeGrade(int grade) { // 평점 수정
        this.grade = grade;
    }
    public void changeText(String text) { // 리뷰 내용 수정
        this.text = text;
    }
}
