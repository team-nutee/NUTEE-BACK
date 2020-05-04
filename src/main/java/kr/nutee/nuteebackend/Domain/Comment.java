package kr.nutee.nuteebackend.Domain;

import kr.nutee.nuteebackend.Domain.common.LogDateTime;
import lombok.*;
import javax.persistence.*;

@Entity
@Getter @Setter @Builder
@NoArgsConstructor @AllArgsConstructor
public class Comment extends LogDateTime {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String content;

    private boolean isDeleted;

    @ManyToOne
    @JoinColumn(name = "member_id")
    private Member member;

    @ManyToOne
    @JoinColumn(name = "post_id")
    private Post post;

    @ManyToOne
    @JoinColumn(name = "parent_id")
    private Comment parent;
}
