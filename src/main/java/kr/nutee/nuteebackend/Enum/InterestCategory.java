package kr.nutee.nuteebackend.Enum;

import lombok.Getter;

@Getter
public enum InterestCategory {
    FREE("FREE"),
    DORMITORY("DORMITORY"),
    FOOD("FOOD"),
    LOVE("LOVE"),
    TRIP("TRIP"),
    JOB("JOB"),
    MARKET("MARKET"),
    STUDY("STUDY"),
    PROMOTION("PROMOTION"),
    ANIMAL("ANIMAL"),
    CERTIFICATE("CERTIFICATE");
    private final String interest;

    InterestCategory(String interest) {
        this.interest = interest;
    }
}
