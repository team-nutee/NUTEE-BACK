package kr.nutee.nuteebackend.DTO.Response;


import lombok.*;

import java.io.Serializable;

@Data
@NoArgsConstructor
@Builder
public class User implements Serializable {
    private Long id;
    private String nickname;
    private ImageResponse image;


    public User(Long id, String nickname, ImageResponse image) {
        this.id = id;
        this.nickname = nickname;
        this.image = image;
    }
}
