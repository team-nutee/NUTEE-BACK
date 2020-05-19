package kr.nutee.nuteebackend.DTO;

import com.querydsl.core.annotations.QueryProjection;
import lombok.*;

import java.io.Serializable;

@Data
@Builder
public class Like implements Serializable {

    int id;

    @QueryProjection
    public Like(int id) {
        this.id = id;
    }
}
