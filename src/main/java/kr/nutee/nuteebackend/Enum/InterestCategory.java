package kr.nutee.nuteebackend.Enum;

import lombok.Getter;

@Getter
public enum InterestCategory {
    FREE("자유"),
    DORMITORY("기숙사"),
    FOOD("음식"),
    LOVE("연애"),
    TRIP("여행"),
    JOB("취업"),
    MARKET("장터"),
    STUDY("스터디"),
    PROMOTION("홍보"),
    ANIMAL("반려동물"),
    CERTIFICATE("자격증");
    private final String interest;

    InterestCategory(String interest) {
        this.interest = interest;
    }
}
