package kr.nutee.nuteebackend.DTO.Request;

import lombok.*;

import javax.validation.constraints.NotEmpty;
import java.io.Serializable;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PasswordUpdateRequest implements Serializable {
    @NotEmpty
    String password;

}
