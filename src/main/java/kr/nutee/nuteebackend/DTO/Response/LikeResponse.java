package kr.nutee.nuteebackend.DTO.Response;

import com.querydsl.core.annotations.QueryProjection;
import lombok.*;

import java.io.Serializable;

@Data
@NoArgsConstructor
@Builder
public class LikeResponse implements Serializable {

    Long id;

    @QueryProjection
    public LikeResponse(Long id) {
        this.id = id;
    }
}
