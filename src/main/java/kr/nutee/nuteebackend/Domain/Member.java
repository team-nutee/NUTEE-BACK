package kr.nutee.nuteebackend.Domain;

import kr.nutee.nuteebackend.Enum.Interest;
import kr.nutee.nuteebackend.Enum.Major;
import kr.nutee.nuteebackend.Enum.RoleType;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Getter @Setter
@Builder @NoArgsConstructor @AllArgsConstructor
public class Member {
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

    @CreationTimestamp
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;

    private LocalDateTime accessedAt;

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

