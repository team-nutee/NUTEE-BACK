package kr.nutee.nuteebackend.DTO.MessageQueue;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@AllArgsConstructor
public class MemberMessage {
    String method;
    MemberDTO origin;
    MemberDTO change;
}
