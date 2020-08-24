package kr.nutee.nuteebackend.DTO.Request;

import lombok.*;
import javax.validation.constraints.NotEmpty;
import java.io.Serializable;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NicknameUpdateRequest implements Serializable {
    @NotEmpty
    String nickname;

    @Override
    public String toString() {
        return "NicknameUpdateRequest{" +
                "nickname='" + nickname + '\'' +
                '}';
    }
}
