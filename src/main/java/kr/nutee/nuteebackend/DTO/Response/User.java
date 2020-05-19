package kr.nutee.nuteebackend.DTO.Response;

import com.querydsl.core.annotations.QueryProjection;
import lombok.*;

import java.io.Serializable;

@Data
@NoArgsConstructor
@Builder
public class User implements Serializable {
    private Long id;
    private String nickname;
    private ImageResponse imageResponse;

    @QueryProjection
    public User(Long id, String nickname, ImageResponse imageResponse) {
        this.id = id;
        this.nickname = nickname;
        this.imageResponse = imageResponse;
    }
}
