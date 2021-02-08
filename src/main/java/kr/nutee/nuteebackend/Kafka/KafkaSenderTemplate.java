package kr.nutee.nuteebackend.Kafka;

import kr.nutee.nuteebackend.DTO.MessageQueue.MemberDTO;
import kr.nutee.nuteebackend.DTO.MessageQueue.MemberMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Component;
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.util.concurrent.ListenableFutureCallback;

@Component
@RequiredArgsConstructor
public class KafkaSenderTemplate {
    private final KafkaTemplate<String, MemberMessage> memberKafkaTemplate;

    public void sendCreateRollbackMember(MemberDTO origin, MemberDTO change) {
        MemberMessage message = new MemberMessage("CREATE", origin, change);
        ListenableFuture<SendResult<String, MemberMessage>> future = memberKafkaTemplate
            .send("member-auth", message);

        future.addCallback(new ListenableFutureCallback<>() {
            @Override
            public void onFailure(Throwable ex) {

            }

            @Override
            public void onSuccess(SendResult<String, MemberMessage> result) {

            }
        });
    }

    public void sendUpdateRollbackMember(MemberDTO origin, MemberDTO change) {
        MemberMessage message = new MemberMessage("UPDATE", origin, change);
        ListenableFuture<SendResult<String, MemberMessage>> future = memberKafkaTemplate
            .send("member-auth", message);

        future.addCallback(new ListenableFutureCallback<>() {
            @Override
            public void onFailure(Throwable ex) {

            }

            @Override
            public void onSuccess(SendResult<String, MemberMessage> result) {

            }
        });
    }

}
