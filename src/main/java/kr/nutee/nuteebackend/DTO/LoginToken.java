package kr.nutee.nuteebackend.DTO;

import lombok.Data;

import java.io.Serializable;

@Data
public class LoginToken implements Serializable {

    private static final long serialVersionUID = -7353484588260422449L;
    private String userId;
    private String accessToken;
    private String refreshToken;

}
