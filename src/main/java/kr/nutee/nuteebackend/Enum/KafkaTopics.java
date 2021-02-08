package kr.nutee.nuteebackend.Enum;

import lombok.Getter;

@Getter
public enum KafkaTopics {
    MEMBER_AUTH("member-auth"),
    MEMBER_SNS("member-sns");
    private String topic;
    KafkaTopics(String topic) {
        this.topic = topic;
    }
}
