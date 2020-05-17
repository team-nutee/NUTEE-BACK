package kr.nutee.nuteebackend.DTO;

import lombok.Data;

@Data
public class Token {
    String sub;
    String role;
    String id;
}
