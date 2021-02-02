package kr.nutee.nuteebackend.DTO.MessageQueue;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.Id;
import kr.nutee.nuteebackend.Domain.Image;
import kr.nutee.nuteebackend.Domain.Member;
import kr.nutee.nuteebackend.Enum.RoleType;
import lombok.Builder;
import lombok.Data;

@Data
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
