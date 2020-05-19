package kr.nutee.nuteebackend.DTO;

import com.querydsl.core.annotations.QueryProjection;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class Token {
    String sub;
    String role;
    String id;

    @QueryProjection
    public Token(String sub, String role, String id) {
        this.sub = sub;
        this.role = role;
        this.id = id;
    }
}
