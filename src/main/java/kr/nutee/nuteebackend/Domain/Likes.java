package kr.nutee.nuteebackend.Domain;

import kr.nutee.nuteebackend.Domain.common.LogDateTime;
import lombok.*;
import javax.persistence.*;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Likes extends LogDateTime {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "member_id")
    private Member member;

    @ManyToOne
    @JoinColumn(name = "post_id")
    private Post post;

}
