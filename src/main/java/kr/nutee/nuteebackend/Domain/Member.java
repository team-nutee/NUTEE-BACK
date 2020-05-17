package kr.nutee.nuteebackend.Domain;

import kr.nutee.nuteebackend.Domain.common.LogDateTime;
import kr.nutee.nuteebackend.Enum.RoleType;
import lombok.*;
import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Builder @NoArgsConstructor @AllArgsConstructor
public class Member extends LogDateTime {
    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    @Column(name = "member_id")
    private Long id;

    @Column(nullable=false, unique=true, length=20)
    private String userId;

    @Column(nullable=false, unique=true, length=20)
    private String nickname;

    @Column(nullable=false, length=50)
    private String schoolEmail;

    private String password;

    private LocalDateTime accessedAt;

    @OneToMany (mappedBy = "member")
    private final List<Image> images = new ArrayList<>();

    @OneToMany (mappedBy = "member")
    private final List<Interest> interests = new ArrayList<>();

    @OneToMany (mappedBy = "member")
    private final List<Major> majors = new ArrayList<>();

    @Enumerated(EnumType.STRING)
    @Column(length=20)
    private RoleType role;

    private boolean isDeleted;

    private boolean isBlocked;

}

