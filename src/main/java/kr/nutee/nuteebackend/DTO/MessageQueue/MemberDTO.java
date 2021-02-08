package kr.nutee.nuteebackend.DTO.MessageQueue;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.Id;
import kr.nutee.nuteebackend.Enum.RoleType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class MemberDTO {

    @Id
    private long id;

    private String userId;

    private String nickname;

    private String schoolEmail;

    private String password;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    private LocalDateTime accessedAt;

    private String profileUrl;

    private final List<String> interests = new ArrayList<>();

    private final List<String> majors = new ArrayList<>();

    private RoleType role;

    private boolean isDeleted;

    private boolean isBlocked;

}
