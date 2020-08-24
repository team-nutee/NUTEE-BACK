package kr.nutee.nuteebackend.Domain;


import kr.nutee.nuteebackend.Domain.common.LogDateTime;
import lombok.*;
import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;


@Entity @Setter
@Getter @Builder
@NoArgsConstructor @AllArgsConstructor
public class Comment extends LogDateTime {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "comment_id")
    private Long id;

    private String content;

    private boolean isDeleted;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id")
    private Post post;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_id")
    private Comment parent;

    @OneToMany(mappedBy = "parent")
    @Builder.Default
    private List<Comment> child = new ArrayList<>();
}
