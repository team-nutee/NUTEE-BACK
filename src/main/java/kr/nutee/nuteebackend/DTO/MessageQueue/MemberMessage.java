package kr.nutee.nuteebackend.DTO.MessageQueue;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MemberMessage {
    String method;
    MemberDTO origin;
    MemberDTO change;
}
