package kr.nutee.nuteebackend.DTO.Request;

import lombok.*;

import javax.validation.constraints.NotEmpty;
import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MajorsUpdateRequest {
    @NotEmpty List<String> majors;
}
