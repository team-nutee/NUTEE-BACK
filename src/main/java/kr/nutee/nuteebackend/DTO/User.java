package kr.nutee.nuteebackend.DTO;

import com.querydsl.core.annotations.QueryProjection;
import lombok.*;

import java.io.Serializable;

@Data
@Builder
public class User implements Serializable {
    private Long id;
    private String nickname;
    private Image image;

    @QueryProjection
    public User(Long id, String nickname, Image image) {
        this.id = id;
        this.nickname = nickname;
        this.image = image;
    }
}
