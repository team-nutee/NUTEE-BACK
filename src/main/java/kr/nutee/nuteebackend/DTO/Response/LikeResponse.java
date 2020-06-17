package kr.nutee.nuteebackend.DTO.Response;


import lombok.*;

import java.io.Serializable;

@Data
@NoArgsConstructor
@Builder
public class LikeResponse implements Serializable {

    Long id;


    public LikeResponse(Long id) {
        this.id = id;
    }
}
