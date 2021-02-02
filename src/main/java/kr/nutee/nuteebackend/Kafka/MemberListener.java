package kr.nutee.nuteebackend.Kafka;

import kr.nutee.nuteebackend.DTO.MessageQueue.MemberMessage;
import kr.nutee.nuteebackend.Service.MemberService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class MemberListener {

    private final MemberService memberService;

    private final KafkaSenderTemplate kafkaSenderTemplate;

    @KafkaListener(topics = "member-sns", containerFactory = "memberKafkaListenerContainerFactory")
    public void consumeMember(MemberMessage payload) {
        try {
            if (payload.getMethod().equals("CREATE")) {
                memberService.createUser(payload.getChange());
            }
            if (payload.getMethod().equals("UPDATE")) {
                memberService.deleteUser(payload.getChange());
            }
        } catch (Exception e) {
            if (payload.getMethod().equals("CREATE")) {
                kafkaSenderTemplate.sendCreateRollbackMember(payload.getOrigin(),payload.getChange());
            }
            if (payload.getMethod().equals("UPDATE")) {
                kafkaSenderTemplate.sendUpdateRollbackMember(payload.getOrigin(),payload.getChange());
            }
        }
    }
}
