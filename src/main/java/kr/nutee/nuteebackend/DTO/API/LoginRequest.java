package kr.nutee.nuteebackend.DTO.API;

import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
public class LoginRequest {
    private String userId;
    private String password;
}
