package kr.nutee.nuteebackend.Domain;

import kr.nutee.nuteebackend.Domain.common.LogDateTime;
import kr.nutee.nuteebackend.Enum.Interest;
import kr.nutee.nuteebackend.Enum.Major;
import kr.nutee.nuteebackend.Enum.RoleType;
import lombok.*;
import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter @Setter
@Builder @NoArgsConstructor @AllArgsConstructor
public class Member extends LogDateTime {
    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
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
    private List<Image> images = new ArrayList<>();

    @Enumerated(EnumType.STRING)
    @Column(length=20)
    private RoleType role;

    @Enumerated(EnumType.STRING)
    @Column(length=20)
    private Major major;

    @Enumerated(EnumType.STRING)
    @Column(length=20)
    private Interest interest;

    private boolean isDeleted;

    private boolean isBlocked;

}

