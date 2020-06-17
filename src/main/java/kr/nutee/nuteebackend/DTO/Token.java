package kr.nutee.nuteebackend.DTO;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class Token {
    String sub;
    String role;
    String id;

    public Token(String sub, String role, String id) {
        this.sub = sub;
        this.role = role;
        this.id = id;
    }
}
